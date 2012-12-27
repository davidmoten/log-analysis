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

public class KeysServlet extends HttpServlet {

	private static final String LOG_SERVER_BASE_URL_DEFAULT = "http://localhost:9191";

	private static final long serialVersionUID = 1044384045444686984L;

	private static final String logServerBaseUrl = System.getProperty(
			"log.server.url", LOG_SERVER_BASE_URL_DEFAULT);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String url = logServerBaseUrl + "/keys?table="
				+ req.getParameter("table");

		url = url.replace(" ", "%20");

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
