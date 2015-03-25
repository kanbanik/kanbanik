package com.googlecode.kanbanik.client.components;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class WarningPanel extends SimplePanel {
	public WarningPanel(String message) {
		add(new Label(message));
		setWidth("200px");
		setHeight("100px");
                getElement().getStyle().setOverflowX(com.google.gwt.dom.client.Style.Overflow.AUTO);
	}
}
