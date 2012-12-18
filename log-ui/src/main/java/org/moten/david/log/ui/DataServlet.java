package org.moten.david.log.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class DataServlet extends HttpServlet {

	private static final long serialVersionUID = 1044384045444686984L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO get url from config
		String url = "http://localhost:9191/query?sql="
				+ req.getParameter("sql") + "&start="
				+ req.getParameter("start") + "&interval="
				+ req.getParameter("interval") + "&buckets="
				+ req.getParameter("buckets") + "&metric="
				+ req.getParameter("metric");

		url = url.replace(" ", "%20");
		// TODO properly escape url
		URL u;
		try {
			u = new URI(url).toURL();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		InputStream is = u.openStream();
		String json = IOUtils.toString(is);
		is.close();
		resp.setContentType("application/json");
		resp.getWriter().print(json);
	}
}
