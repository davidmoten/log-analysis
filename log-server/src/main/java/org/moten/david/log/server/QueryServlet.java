package org.moten.david.log.server;

import static org.moten.david.log.server.ServletUtil.getMandatoryDouble;
import static org.moten.david.log.server.ServletUtil.getMandatoryLong;
import static org.moten.david.log.server.ServletUtil.getMandatoryParameter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.moten.david.log.core.Database;
import org.moten.david.log.core.DatabaseOrient;
import org.moten.david.log.core.Field;
import org.moten.david.log.orientdb.SqlSelect;
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
			String sql;
			if (req.getParameter("sql") == null) {
				String field = getMandatoryParameter(req, "field");
				sql = "select " + Field.TIMESTAMP + ", " + Field.PROPS + "["
						+ field + "]." + Field.VALUE + " as " + Field.VALUE
						+ " from " + Database.TABLE_ENTRY + " where "
						+ Field.PROPS + " containskey '" + field + "'"
						+ " order by " + Field.TIMESTAMP;
				if (req.getParameter("text") != null) {
					SqlSelect ss = new SqlSelect(sql);
					// TODO protect against sql injection
					String clause = Field.TEXT + " containstext '"
							+ req.getParameter("text") + "'";
					ss = ss.and(clause);
					sql = ss.toString();
				}
			} else
				sql = req.getParameter("sql");
			long startTime = getMandatoryLong(req, "start");
			double interval = getMandatoryDouble(req, "interval");
			long numBuckets = getMandatoryLong(req, "buckets");
			Metric metric = Metric
					.valueOf(getMandatoryParameter(req, "metric"));
			writeJson(db, sql, startTime, interval, numBuckets, metric,
					resp.getWriter());
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

	private static void writeJson(Database db, String sql, long startTime,
			double interval, long numBuckets, Metric metric, PrintWriter writer) {
		BucketQuery q = new BucketQuery(new Date(startTime), interval,
				numBuckets, sql);
		Buckets buckets = db.execute(q);
		log.info("building json");
		Util.writeJson(buckets, metric, writer);
		log.info("built json");
	}

	private static void setupLogging() throws IOException {
		LogManager.getLogManager().readConfiguration(
				Main.class.getResourceAsStream("/my-logging.properties"));
	}

	public static void main(String[] args) throws IOException {

		setupLogging();
		System.setProperty("network.lockTimeout", "10000");
		Database db = new DatabaseOrient("remote:localhost/logs", "admin", "admin");

		String json = getJson(db, "select " + Field.TIMESTAMP + ", "
				+ Field.PROPS + "[specialNumber]." + Field.VALUE + " as "
				+ Field.VALUE + " from Entry where (" + Field.PROPS
				+ " containskey 'specialNumber') order by " + Field.TIMESTAMP,
				System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1),
				TimeUnit.MINUTES.toMillis(1), 60, Metric.MEAN);
		System.out.println(json);
	}

}
