package com.googlecode.kanbanik.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.googlecode.kanbanik.model.KanbanikConnectionManager;

public class KanbanikContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		// close the connection pool
		new KanbanikConnectionManager().destroyConnection();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		// it takes care about creating a connection in a lazy way
	}

}
