package com.googlecode.kanbanik.client.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PanelContainingDialog extends DialogBox {
	
	private Panel contentPanel;
	
	private Panel mainPanel = new VerticalPanel();
	
	private Panel buttonPanel = new HorizontalPanel();
	
	private List<PanelContainingDialolgListener> listeners;
	
	public PanelContainingDialog(String title, Panel contentPanel) {
		super();
		this.contentPanel = contentPanel;
		
		setupButtons();
		
		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		
		mainPanel.add(contentPanel);
		mainPanel.add(buttonPanel);
		contentPanel.setWidth("300px");
		contentPanel.setHeight("200px");
		add(mainPanel);
	}

	private void setupButtons() {
		Button okButton = new Button("OK");
		okButton.addClickHandler(new OKButtonHandler());
		
		Button cancelButton = new Button("cancel");
		cancelButton.addClickHandler(new CancelButtonHandler());
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
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
			hide();
	
			if (listeners == null) {
				return;
			}
			
			for (PanelContainingDialolgListener listener : listeners) {
				listener.okClicked(PanelContainingDialog.this);
			}
		}
		
	}

	public Panel getContentPanel() {
		return contentPanel;
	}

	public static interface PanelContainingDialolgListener {
		
		void okClicked(PanelContainingDialog dialog);
		
		void cancelClicked(PanelContainingDialog dialog);
	}
	
}
