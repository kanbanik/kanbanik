package com.googlecode.kanbanik.server;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AbstractServiceServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = -3380627852524031779L;

	@SuppressWarnings("unchecked")
	protected <T> T getBean(String beanName) {
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		return (T) applicationContext.getBean(beanName);
	}
}
