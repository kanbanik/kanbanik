package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;

public class KanbanikEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		new KanbanikModuleManager().initialize();
	
	}
	
}