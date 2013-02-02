package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;

public abstract class AbstractDeletingComponent implements ClickHandler, Closable {

	private PanelContainingDialog yesNoDialog;

	private WarningPanel warningPanel;

	public AbstractDeletingComponent(HasClickHandlers hasClickHandler) {
		hasClickHandler.addClickHandler(this);
	}
	
	public AbstractDeletingComponent() {
		
	}

	public void onClick(ClickEvent event) {
		warningPanel = new WarningPanel("Are you sure you want to delete " + getMessageSpecificPart() + "?");
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener());
		yesNoDialog.center();	
	}

	@Override
	public void close() {
		yesNoDialog.close();
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
