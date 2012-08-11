package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NoContentWarningPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, NoContentWarningPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	Label messageLabel;
	
	public NoContentWarningPanel(String message) {
		initWidget(uiBinder.createAndBindUi(this));
		messageLabel.setText(message);
	}
}
