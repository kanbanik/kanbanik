package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.workflowitem.WorkflowitemChangedMessage;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowitemEditingComponent implements PanelContainingDialolgListener, ClickHandler, MessageListener<WorkflowitemDto> {

	private WorkflowItemEditPanel panel;

	private WorkflowitemDto dto;
	
	private PanelContainingDialog dialog;
	
	public WorkflowitemEditingComponent(WorkflowitemDto dto, HasClickHandlers clickHandlers) {
		super();
		this.dto = dto;
		clickHandlers.addClickHandler(this);
		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	private WorkflowItemEditPanel toPanel() {
		WorkflowItemEditPanel panel = new WorkflowItemEditPanel();
		renderDto(panel);
		return panel;
	}

	private void renderDto(WorkflowItemEditPanel panel) {
		panel.setName(dto.getName());
		panel.setWipLimit(dto.getWipLimit());
		
		panel.setVerticalSizing(dto.getVerticalSize() != -1, dto.getVerticalSize());
		
		if (dto.getNestedWorkflow().getWorkflowitems().size() != 0) {
			panel.setVerticalSizingEnabled(false, "Can not be set for non-leaf workflowitems");
		}
		
		
		panel.setType(dto.getItemType());
	}
	
	private void flushDto() {
		dto.setName(panel.getName());
		dto.setWipLimit(panel.getWipLimit());
		dto.setItemType(panel.getItemType());
		dto.setVerticalSize(panel.getVerticalSize());
	}

	public void onClick(ClickEvent event) {
		panel = toPanel();
		dialog = new PanelContainingDialog("Edit Workflow Item", panel, panel.getDefaultFocusWidget());
		dialog.addListener(this);
		dialog.center();
	}
	
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}

	public void okClicked(final PanelContainingDialog dialog) {
		flushDto();
		
		new KanbanikServerCaller(
				new Runnable() {
					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<WorkflowitemDto>, FailableResult<SimpleParams<WorkflowitemDto>>> invokeCommand(
				ServerCommand.EDIT_WORKFLOWITEM_DATA,
				new SimpleParams<WorkflowitemDto>(dto),
				new ResourceClosingAsyncCallback<FailableResult<SimpleParams<WorkflowitemDto>>>(dialog) {

					@Override
					public void success(FailableResult<SimpleParams<WorkflowitemDto>> result) {
						MessageBus.sendMessage(new WorkflowitemChangedMessage(result.getPayload().getPayload(), this));
						MessageBus.sendMessage(new BoardsRefreshRequestMessage(result.getPayload().getPayload().getParentWorkflow().getBoard(), this));
					}
				}); 
		}});		
	}

	public void messageArrived(Message<WorkflowitemDto> message) {
		if (dto.getId() != null && dto.getId().equals(message.getPayload().getId())) {
			dto = message.getPayload();
		}
	}
}
