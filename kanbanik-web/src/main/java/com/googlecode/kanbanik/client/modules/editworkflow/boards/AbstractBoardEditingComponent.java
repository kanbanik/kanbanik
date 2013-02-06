package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;

public abstract class AbstractBoardEditingComponent implements PanelContainingDialolgListener, Closable, Component<BoardWithProjectsDto> {
	
	private Panel mainPanel = new VerticalPanel();
	
	private HorizontalPanel namePanel = new HorizontalPanel();
	
	private HorizontalPanel balancedWorkflowPanel = new HorizontalPanel();

	private Label boardNameLabel = new Label("Board Name: ");

	private TextBox boardNameText = new TextBox(); 
	
	private Label balanceWorkflowitemsLabel = new Label("Balance space of workflowitems (can be slow for complex flows)");
	
	private CheckBox balanceWorkflowitems = new CheckBox();

	private PanelContainingDialog dialog;
	
	public AbstractBoardEditingComponent(HasClickHandlers hasClickHandler, String title) {
		setup(hasClickHandler, title);
	}
	
	public AbstractBoardEditingComponent() {
		
	}
	
	@Override
	public void setup(HasClickHandlers hasClickHandler, String title) {
		boardNameLabel.setWidth("160px");
		
		namePanel.add(boardNameLabel);
		namePanel.add(boardNameText);
		
		balanceWorkflowitemsLabel.setWidth("160px");
		balancedWorkflowPanel.add(balanceWorkflowitemsLabel);
		balancedWorkflowPanel.add(balanceWorkflowitems);
		
		mainPanel.add(namePanel);
		mainPanel.add(balancedWorkflowPanel);
		
		mainPanel.setWidth("300px");
		
		dialog = new PanelContainingDialog(title, mainPanel, boardNameText);
		dialog.addListener(this);
		hasClickHandler.addClickHandler(new ShowDialogHandler());		
	}
	
	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			boardNameText.setText(getBoardName());
			balanceWorkflowitems.setValue(isBalancedWorkflow());
			dialog.center();
			boardNameText.setFocus(true);
		}

	}

	public void okClicked(PanelContainingDialog dialog) {
		BoardDto dto = new BoardDto();
		dto.setName(boardNameText.getText());
		dto.setBalanceWorkflowitems(balanceWorkflowitems.getValue());
		onOkClicked(dto);
	}
	
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}
	
	@Override
	public void setDto(BoardWithProjectsDto dto) {
		
	}
	
	@Override
	public void close() {
		dialog.close();
	}
	
	protected abstract String getBoardName();
	
	protected abstract boolean isBalancedWorkflow();
	
	protected abstract void onOkClicked(BoardDto dto);
}
