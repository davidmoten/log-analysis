package org.moten.david.log.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoadDummyRecordsServlet extends HttpServlet {

	private static final long serialVersionUID = 5743443079037803453L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		long n = ServletUtil.getLong(req, "n", 1000);
		ServletUtil.connectToDatabase().persistDummyRecords(n);
		resp.getWriter().print("loaded");
	}

}
