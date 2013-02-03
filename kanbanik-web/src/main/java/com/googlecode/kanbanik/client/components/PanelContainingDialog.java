package com.googlecode.kanbanik.client.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

public class PanelContainingDialog extends DialogBox implements Closable {

	interface MyUiBinder extends UiBinder<Widget, PanelContainingDialog> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private List<PanelContainingDialolgListener> listeners;

	private final FocusWidget focusWidget;

	@UiField
	Button cancelButton;
	
	@UiField
	Button okButton;

	@UiField
	FlowPanel contentWrapper;
	
	public PanelContainingDialog(String title, Widget contentPanel) {
		this(title, contentPanel, null);
	}
	
	public PanelContainingDialog(String title, Widget contentPanel, FocusWidget focusWidget) {
		super();
		
		setWidget(uiBinder.createAndBindUi(this));
		
		this.focusWidget = focusWidget;
		
		contentWrapper.add(contentPanel);
		
		setupButtons();
		
		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		
	}

	private void setupButtons() {
		okButton.addClickHandler(new OKButtonHandler());
		okButton.setText(" OK ");
		
		cancelButton.addClickHandler(new CancelButtonHandler());
		cancelButton.setText(" Cancel ");
	}
	
	public void addListener(PanelContainingDialolgListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<PanelContainingDialog.PanelContainingDialolgListener>();
		}
		
		listeners.add(listener);
	}
	
	class CancelButtonHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			hide();
			
			if (listeners == null) {
				return;
			}
			
			for (PanelContainingDialolgListener listener : listeners) {
				listener.cancelClicked(PanelContainingDialog.this);
			}
			
		}
		
	}
	
	class OKButtonHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			if (listeners == null) {
				return;
			}
			
			for (PanelContainingDialolgListener listener : listeners) {
				listener.okClicked(PanelContainingDialog.this);
			}
		}
		
	}

	public static interface PanelContainingDialolgListener {
		
		void okClicked(PanelContainingDialog dialog);
		
		void cancelClicked(PanelContainingDialog dialog);
	}

	@Override
	public void close() {
		hide();
	}
	
	@Override
	public void center() {
		super.center();
		
		// it has to be scheduled deferred because the focus has to be taken after the button takes it
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (focusWidget != null) {
					focusWidget.setFocus(true);
				} else {
					cancelButton.setFocus(true);
				}
			}
		});
	}
	
}
