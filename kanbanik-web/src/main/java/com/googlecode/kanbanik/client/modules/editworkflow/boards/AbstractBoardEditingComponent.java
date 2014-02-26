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
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;

public abstract class AbstractBoardEditingComponent implements PanelContainingDialolgListener, Closable, Component<Dtos.BoardWithProjectsDto> {
	
	private Panel mainPanel = new VerticalPanel();
	
	private HorizontalPanel namePanel = new HorizontalPanel();
	
	private HorizontalPanel balancedWorkflowPanel = new HorizontalPanel();

	private Label boardNameLabel = new Label("Board Name: ");

	private TextBox boardNameText = new TextBox(); 
	
	private Label workflowVerticalSizingLabel = new Label("Balance vertical items' sizes");
	
	private CheckBox workflowVerticalSizing = new CheckBox();
	
	private Label showUserPictureLabel = new Label("Show assignee picture on tasks");
	
	private CheckBox showUserPictureBox = new CheckBox();

	private HorizontalPanel showUserPanel = new HorizontalPanel();
	
	
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
		
		balancedWorkflowPanel.add(workflowVerticalSizingLabel);
		balancedWorkflowPanel.add(workflowVerticalSizing);
		
		showUserPanel.add(showUserPictureLabel);
		showUserPanel.add(showUserPictureBox);
		
		mainPanel.add(namePanel);
		mainPanel.add(balancedWorkflowPanel);
		mainPanel.add(showUserPanel);
		
		mainPanel.setWidth("300px");
		
		dialog = new PanelContainingDialog(title, mainPanel, boardNameText);
		dialog.addListener(this);
		hasClickHandler.addClickHandler(new ShowDialogHandler());		
	}
	
	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			boardNameText.setText(getBoardName());
			workflowVerticalSizing.setValue(getVerticalSizing() == Dtos.WorkflowVerticalSizing.BALANCED);
			
			showUserPictureBox.setValue(isUserPictureDisplayingEnabled());
			dialog.center();
			boardNameText.setFocus(true);
		}

	}

	public void okClicked(PanelContainingDialog dialog) {
		Dtos.BoardDto dto = DtoFactory.boardDto();
		dto.setName(boardNameText.getText());
		dto.setWorkflowVerticalSizing(workflowVerticalSizing.getValue() ? Dtos.WorkflowVerticalSizing.BALANCED.getSizing() : Dtos.WorkflowVerticalSizing.MIN_POSSIBLE.getSizing());
		
		dto.setShowUserPictureEnabled(showUserPictureBox.getValue());
		onOkClicked(dto);
	}
	
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}
	
	@Override
	public void setDto(Dtos.BoardWithProjectsDto dto) {
		
	}
	
	@Override
	public void close() {
		dialog.close();
	}
	
	protected abstract boolean isUserPictureDisplayingEnabled();
	
	protected abstract String getBoardName();
	
	protected abstract Dtos.WorkflowVerticalSizing getVerticalSizing();
	
	protected abstract void onOkClicked(Dtos.BoardDto dto);
}
