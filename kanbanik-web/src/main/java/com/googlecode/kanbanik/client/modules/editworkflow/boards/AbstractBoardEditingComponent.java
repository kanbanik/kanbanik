package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.dto.BoardDto;

public abstract class AbstractBoardEditingComponent implements PanelContainingDialolgListener {
	
	protected final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);
	
	private Panel panel = new HorizontalPanel();

	private Label boardNameLabel = new Label("Board Name: ");

	private TextBox boardNameText = new TextBox(); 

	private PanelContainingDialog dialog;
	
	public AbstractBoardEditingComponent(HasClickHandlers hasClickHandler, String title) {
		panel.add(boardNameLabel);
		panel.add(boardNameText);
		dialog = new PanelContainingDialog(title, panel);
		dialog.addListener(this);
		hasClickHandler.addClickHandler(new ShowDialogHandler());
	}
	
	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			boardNameText.setText(getBoardName());
			dialog.center();
			boardNameText.setFocus(true);
		}

	}

	public void okClicked(PanelContainingDialog dialog) {
		BoardDto dto = new BoardDto();
		dto.setName(boardNameText.getText());
		onOkClicked(dto);
	}
	
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}
	
	protected abstract String getBoardName();
	
	protected abstract void onOkClicked(BoardDto dto);
}
