package org.moten.david.log.server;

import java.io.IOException;
import java.util.Date;

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

	private static final long serialVersionUID = 5553574830587263509L;

	private final Database db = new Database("remote:jenkins.amsa.gov.au/logs",
			"admin", "admin");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String sql = getMandatoryParameter(req, "sql");
		long startTime = getMandatoryLong(req, "start");
		double interval = getMandatoryDouble(req, "interval");
		long numBuckets = getMandatoryLong(req, "buckets");
		Metric metric = Metric.valueOf(getMandatoryParameter(req, "metric"));
		BucketQuery q = new BucketQuery(new Date(startTime), interval,
				numBuckets, sql);
		Buckets buckets = db.execute(q);
		resp.getWriter().print(Util.toJson(buckets, metric));
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
			throw new RuntimeException("parameter " + name + " is mandatory");
		else
			try {
				return Long.parseLong(req.getParameter(name));
			} catch (NumberFormatException e) {
				throw new RuntimeException("parameter " + name
						+ " parsing problem", e);
			}
	}

	private String getMandatoryParameter(HttpServletRequest req, String name) {
		if (req.getParameter(name) != null)
			return req.getParameter(name);
		else
			throw new RuntimeException("parameter " + name + " is mandatory");
	}

}
