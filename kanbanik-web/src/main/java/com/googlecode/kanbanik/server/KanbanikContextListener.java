package com.googlecode.kanbanik.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.kanbanik.model.KanbanikConnectionManager;

public class KanbanikContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		// close the connection pool
		new KanbanikConnectionManager().destroyConnectionPool();
	}

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		String server = context.getInitParameter("mongoServer");
		String port = context.getInitParameter("mongoPort");
		String user = context.getInitParameter("mongoUser");
		String password = context.getInitParameter("mongoPassword");
		String dbName = context.getInitParameter("mongoDBName");
		String authenticationRequired = context.getInitParameter("mongoAuthenticationRequired");
		
		new KanbanikConnectionManager().initConnectionPool(
				server,
				port,
				user,
				password,
				dbName,
				authenticationRequired
				);
	}

}
