package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowitemEditingComponent implements PanelContainingDialolgListener, ClickHandler {

	private WorkflowItemEditPanel panel;

	private WorkflowitemDto dto;
	
	private PanelContainingDialog dialog;
	
	public WorkflowitemEditingComponent(WorkflowitemDto dto, HasClickHandlers clickHandlers) {
		super();
		this.dto = dto;
		clickHandlers.addClickHandler(this);
	}

	private WorkflowItemEditPanel toPanel() {
		WorkflowItemEditPanel panel = new WorkflowItemEditPanel();
		panel.setName(dto.getName());
		panel.setWipLimit(dto.getWipLimit());
		panel.setType(dto.getItemType());
		return panel;
	}
	
	private void flushDto() {
		dto.setName(panel.getName());
		dto.setWipLimit(panel.getWipLimit());
		dto.setItemType(panel.getItemType());
	}

	public void onClick(ClickEvent event) {
		panel = toPanel();
		dialog = new PanelContainingDialog("Edit Workflow Item", panel);
		dialog.addListener(this);
		dialog.center();
	}
	
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}

	public void okClicked(PanelContainingDialog dialog) {
		flushDto();
		
		new KanbanikServerCaller(
				new Runnable() {
					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<WorkflowitemDto>, VoidParams> invokeCommand(
				ServerCommand.EDIT_WORKFLOWITEM_DATA,
				new SimpleParams<WorkflowitemDto>(dto),
				new KanbanikAsyncCallback<VoidParams>() {

					@Override
					public void success(VoidParams result) {
						MessageBus.sendMessage(new RefreshBoardsRequestMessage("", this));
					}
				}); 
		}});		
	}
}
