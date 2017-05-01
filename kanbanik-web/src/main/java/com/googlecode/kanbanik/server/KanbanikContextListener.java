package com.googlecode.kanbanik.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.kanbanik.Configuration;
import com.googlecode.kanbanik.model.KanbanikConnectionManager;
import com.googlecode.kanbanik.security.KanbanikRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;

public class KanbanikContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		// close the connection pool
		new KanbanikConnectionManager().destroyConnectionPool();
	}

	public void contextInitialized(ServletContextEvent event) {
        SecurityUtils.setSecurityManager(new DefaultSecurityManager(new KanbanikRealm()));

		ServletContext context = event.getServletContext();
		String server = getParam(context, "MONGODB_HOST");
		String port = getParam(context, "MONGODB_PORT");
		String user = getParam(context, "MONGODB_USER");
		String password = getParam(context, "MONGODB_PASSWORD");
		String dbName = getParam(context, "MONGODB_DATABASE");
		String authenticationRequired = getParam(context, "MONGODB_AUTHENTICATION_REQUIRED");

		boolean enableGzipCommunication = Boolean.parseBoolean(getParam(context, "ENABLE_GZIP_COMMUNICATION"));
		boolean enableAccessControlHeaders = Boolean.parseBoolean(getParam(context, "ENABLE_ACCESS_CONTROL_HEADERS"));
        Configuration.init(enableGzipCommunication, enableAccessControlHeaders);

		new KanbanikConnectionManager().initConnectionPool(
				server,
				port,
				user,
				password,
				dbName,
				authenticationRequired
				);
	}

	private String getParam(ServletContext context, String paramName) {
		try {
			String fromEnv = System.getenv(paramName);
			if (fromEnv != null) {
				return fromEnv;
			}
		} catch (SecurityException e) {
		}
		return context.getInitParameter(paramName);
	}
}
