package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.GWT;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;

public class ServerCommandInvokerManager {
	private static final ServerCommandInvokerAsync serverCommandInvoker = GWT.create(ServerCommandInvoker.class);
	
	public static final ServerCommandInvokerAsync getInvoker() {
		return serverCommandInvoker;
	}
	
}
