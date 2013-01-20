package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.modules.LoginModule;
import com.googlecode.kanbanik.client.security.CurrentUser;

public class KanbanikEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		
		for (int i = 0; i < RootPanel.get("mainSection").getWidgetCount(); i ++) {
			RootPanel.get("mainSection").remove(i);
		}
		
		if (!CurrentUser.getInstance().isLoogedIn()) {
			RootPanel.get("mainSection").add(new LoginModule(this));
		} else {
			RootPanel.get("mainSection").add(new Label("Logged in as: " + CurrentUser.getInstance().getUser().getUserName()));
			RootPanel.get("mainSection").add(new ControlPanelModule());
		}
	}
}