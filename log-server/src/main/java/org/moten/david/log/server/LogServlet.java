package org.moten.david.log.server;

import static org.moten.david.log.server.ServletUtil.getMandatoryLong;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.moten.david.log.core.Database;

public class LogServlet extends HttpServlet {

	private static final long serialVersionUID = 6408854941290869409L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Database db = ServletUtil.connectToDatabase();
		try {
			long startTime = getMandatoryLong(req, "start");
			long finishTime = getMandatoryLong(req, "finish");
			PrintWriter out = resp.getWriter();
			for (String line : db.getLogs(startTime, finishTime)) {
				if (line != null)
					out.println(line);
			}
		} finally {
			db.close();
		}
	}

}
