package com.googlecode.kanbanik.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.googlecode.kanbanik.migrate.MigrateDb;

public class MigrateDbListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		new MigrateDb().migrateDbIfNeeded();
	}

}
