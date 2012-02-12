package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;

public class KanbanikEntryPoint implements EntryPoint{
	
	public void onModuleLoad() {
		RootPanel.get("mainSection").add(new ControlPanelModule());
	}

}

