package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.providers.DtoProviders;

public class KanbanikEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		initProviders();
		
		RootPanel.get("mainSection").add(new ControlPanelModule());
	}

	private void initProviders() {
		DtoProviders.projectDtoProvider.initialize();
	}

	
}