package com.googlecode.kanbanik;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractIntegrationTest {

	@Autowired
	protected Kanbanik kanbanik;

	protected EntityManager manager;
	
	@PersistenceContext
	public void setManager(EntityManager manager) {
		this.manager = manager;
	}
	
	@Transactional
	@Before
	public void setup() throws Exception {
		Connection conn = ((org.hibernate.impl.SessionImpl)manager.getDelegate()).connection();
		new DefaultDatabaseTester(new HsqldbConnection(conn, null));
		DefaultDatabaseTester tester = new DefaultDatabaseTester(new HsqldbConnection(conn, null));
		tester.setDataSet(new XmlDataSet(getClass().getResourceAsStream(getDataset())));
		tester.onSetup();
	}
	
	protected abstract String getDataset();
}
