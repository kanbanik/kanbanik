package com.googlecode.kanbanik.client.components;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorDialog extends DialogBox implements ClickHandler {

	private Button closeButton;
	
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
		closeButton = new Button("close");
		closeButton.addClickHandler(this);
		panel.add(new Label(message));
		panel.add(closeButton);
		panel.setWidth("200px");
		add(panel);
	}

	public void onClick(ClickEvent event) {
		hide();
	}
	
	@Override
	public void center() {
		super.center();

		// it has to be scheduled deferred because the focus has to be taken after the button takes it
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				closeButton.setFocus(true);
			}
		});
	}
}
