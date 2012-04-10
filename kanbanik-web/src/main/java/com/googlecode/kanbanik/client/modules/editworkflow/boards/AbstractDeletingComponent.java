package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;

public abstract class AbstractDeletingComponent implements ClickHandler {

	private PanelContainingDialog yesNoDialog;

	private SimplePanel warningPanel;

	public AbstractDeletingComponent(HasClickHandlers hasClickHandler) {
		hasClickHandler.addClickHandler(this);
	}

	public void onClick(ClickEvent event) {
		warningPanel = new SimplePanel();
		warningPanel.add(new Label("Are you sure you want to delete " + getMessageSpecificPart() + "?"));
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener());
		yesNoDialog.center();	
	}

	protected abstract String getMessageSpecificPart();
	protected abstract void onOkClicked();

	class YesNoDialogListener implements PanelContainingDialolgListener {

		public void okClicked(PanelContainingDialog dialog) {
			onOkClicked();
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}
}
