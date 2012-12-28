package org.moten.david.log.server;

import static org.moten.david.log.server.ServletUtil.connectToDatabase;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.moten.david.log.core.Database;

public class KeysServlet extends HttpServlet {

	private static final long serialVersionUID = -3184000623032411079L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Database db = connectToDatabase();
		StringBuilder s = new StringBuilder();
		for (String key : db.getKeys()) {
			if (s.length() > 0)
				s.append(",");
			s.append("\"");
			s.append(key);
			s.append("\"");
		}
		resp.setContentType("application/json");
		resp.getWriter().print("{ \"keys\": [" + s.toString() + "] }");
	}
}
