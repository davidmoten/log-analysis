package org.moten.david.log.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.moten.david.log.core.Database;

public class LoadDummyRecordsServlet extends HttpServlet {

	private static final long serialVersionUID = 5743443079037803453L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Database db = ServletUtil.connectToDatabase();
		try {
			if ("true".equalsIgnoreCase(req.getParameter("configure"))) {
				db.configureDatabase();
				db.close();
				db = ServletUtil.connectToDatabase();
			}
			long n = ServletUtil.getLong(req, "n", 1000);
			db.persistDummyRecords(n);
			resp.getWriter().print("done");
		} finally {
			db.close();
		}

	}
}
