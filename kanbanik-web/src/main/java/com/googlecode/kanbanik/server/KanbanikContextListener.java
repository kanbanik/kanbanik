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
		String server = getParam(context, "mongoServer");
		String port = getParam(context, "mongoPort");
		String user = getParam(context, "mongoUser");
		String password = getParam(context, "mongoPassword");
		String dbName = getParam(context, "mongoDBName");
		String authenticationRequired = getParam(context, "mongoAuthenticationRequired");

		boolean enableGzipCommunication = Boolean.parseBoolean(getParam(context, "enableGzipCommunication"));
		boolean enableAccessControlHeaders = Boolean.parseBoolean(getParam(context, "enableAccessControlHeaders"));
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
