package com.googlecode.kanbanik.client.components;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorDialog extends DialogBox implements ClickHandler {

	public ErrorDialog(Throwable throwable) {
		this(throwable.getMessage());
	}
	
	public ErrorDialog(String message) {
		setTitle("Error!");
		setText("Error");
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		
		VerticalPanel panel = new VerticalPanel();
		Button closeButton = new Button("close");
		closeButton.addClickHandler(this);
		panel.add(new Label(message));
		panel.add(closeButton);
		panel.setWidth("300px");
		panel.setHeight("200px");
		add(panel);
	}

	public void onClick(ClickEvent event) {
		hide();
	}
}
