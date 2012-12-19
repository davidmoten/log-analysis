package org.moten.david.log.server;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseFactory;
import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;
import org.moten.david.log.query.Metric;
import org.moten.david.log.query.Util;

import com.orientechnologies.orient.server.OServerShutdownMain;

public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = 5553574830587263509L;

	private final DatabaseFactory factory = new DatabaseFactory(
			System.getProperty("db.url", "remote:localhost/logs"), "admin",
			"admin");

	private static boolean haveConfigured = false;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		OServerShutdownMain m;
		Database db = getDatabase(factory);
		try {
			String sql = getMandatoryParameter(req, "sql");
			long startTime = getMandatoryLong(req, "start");
			double interval = getMandatoryDouble(req, "interval");
			long numBuckets = getMandatoryLong(req, "buckets");
			Metric metric = Metric
					.valueOf(getMandatoryParameter(req, "metric"));
			String json = getJson(db, sql, startTime, interval, numBuckets,
					metric);
			resp.getWriter().print(json);
		} finally {
			db.close();
		}
	}

	private synchronized Database getDatabase(DatabaseFactory factory) {
		Database db = factory.create();
		if (!haveConfigured) {
			db.configureDatabase();
			db.persistDummyRecords();
		}
		haveConfigured = true;
		return db;
	}

	private static String getJson(Database db, String sql, long startTime,
			double interval, long numBuckets, Metric metric) {
		BucketQuery q = new BucketQuery(new Date(startTime), interval,
				numBuckets, sql);
		Buckets buckets = db.execute(q);
		String json = Util.toJson(buckets, metric);
		return json;
	}

	private double getMandatoryDouble(HttpServletRequest req, String name) {
		if (req.getParameter(name) == null)
			throw new RuntimeException("parameter " + name + " is mandatory");
		else
			try {
				return Double.parseDouble(req.getParameter(name));
			} catch (NumberFormatException e) {
				throw new RuntimeException("parameter " + name
						+ " parsing problem", e);
			}
	}

	private long getMandatoryLong(HttpServletRequest req, String name) {
		if (req.getParameter(name) == null)
			throw new RuntimeException("parameter '" + name + "' is mandatory");
		else
			try {
				return Long.parseLong(req.getParameter(name));
			} catch (NumberFormatException e) {
				throw new RuntimeException("parameter '" + name
						+ "' could not be parsed as a Long: "
						+ req.getParameter(name), e);
			}
	}

	private String getMandatoryParameter(HttpServletRequest req, String name) {
		if (req.getParameter(name) != null)
			return req.getParameter(name);
		else
			throw new RuntimeException("parameter " + name + " is mandatory");
	}

	public static void main(String[] args) {

		Database db = new Database("remote:jenkins.amsa.gov.au/logs", "admin",
				"admin");
		String json = getJson(
				db,
				"select logTimestamp, rateMsgPerSecond as value from Entry where rateMsgPerSecond is not null",
				System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1),
				TimeUnit.MINUTES.toMillis(1), 60, Metric.MEAN);
		System.out.println(json);
	}
}
