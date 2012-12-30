package org.moten.david.log.server;

import static org.moten.david.log.server.ServletUtil.getMandatoryDouble;
import static org.moten.david.log.server.ServletUtil.getMandatoryLong;
import static org.moten.david.log.server.ServletUtil.getMandatoryParameter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.moten.david.log.core.Database;
import org.moten.david.log.query.BucketQuery;
import org.moten.david.log.query.Buckets;
import org.moten.david.log.query.Metric;
import org.moten.david.log.query.Util;

public class QueryServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(QueryServlet.class
			.getName());

	private static final long serialVersionUID = 5553574830587263509L;
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Database db = ServletUtil.connectToDatabase();
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

	private static String getJson(Database db, String sql, long startTime,
			double interval, long numBuckets, Metric metric) {
		BucketQuery q = new BucketQuery(new Date(startTime), interval,
				numBuckets, sql);
		Buckets buckets = db.execute(q);
		log.info("building json");
		String json = Util.toJson(buckets, metric);
		log.info("build json");
		return json;
	}

	public static void main(String[] args) throws IOException {
		
		setupLogging();
		System.setProperty("network.lockTimeout", "60000");
		System.setProperty("network.socketTimeout", "60000");
		Database db = new Database("remote:localhost/logs", "admin", "admin");
		// db.configureDatabase();
		// db.persistDummyRecords(100000);
		String json = getJson(
				db,
				"select logTimestamp, logProps[specialNumber].logValue as logValue from Entry where (logProps containskey 'specialNumber') order by logTimestamp",
				System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1),
				TimeUnit.MINUTES.toMillis(1), 60, Metric.MEAN);
		System.out.println(json);
	}

	private static void setupLogging() throws IOException {
		LogManager.getLogManager().readConfiguration(
				Main.class.getResourceAsStream("/my-logging.properties"));
	}

}
