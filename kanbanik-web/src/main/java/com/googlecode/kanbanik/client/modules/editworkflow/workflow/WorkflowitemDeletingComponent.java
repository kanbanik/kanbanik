package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.workflowitem.WorkflowitemChangedMessage;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowitemDeletingComponent implements ClickHandler, Closable, MessageListener<WorkflowitemDto> {

	private PanelContainingDialog yesNoDialog;
	
	private WarningPanel warningPanel;

	private WorkflowitemDto dto;
	
	public WorkflowitemDeletingComponent(WorkflowitemDto dto, HasClickHandlers clickHandler) {
		this.dto = dto;
		clickHandler.addClickHandler(this);
		warningPanel = new WarningPanel("Are you sure to delete this workflowitem '" + dto.getName() + "' ?");
		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	public void onClick(ClickEvent event) {
		if (dto.getId() == null) {
			return;
		}

		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener());
		yesNoDialog.center();
	}
	
	@Override
	public void close() {
		yesNoDialog.close();
	}

class YesNoDialogListener implements PanelContainingDialolgListener {
		
		public YesNoDialogListener() {
		}

		public void okClicked(PanelContainingDialog dialog) {
			
			new KanbanikServerCaller(
					new Runnable() {
						public void run() {
			ServerCommandInvokerManager.getInvoker().<SimpleParams<WorkflowitemDto>, FailableResult<VoidParams>> invokeCommand(
					ServerCommand.DELETE_WORKFLOWITEM,
					new SimpleParams<WorkflowitemDto>(dto),
					new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(WorkflowitemDeletingComponent.this) {

						@Override
						public void success(FailableResult<VoidParams> result) {
							MessageBus.sendMessage(new BoardsRefreshRequestMessage("", this));
						}
					});
			}});
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

	public void messageArrived(Message<WorkflowitemDto> message) {
		if (dto.getId() != null && dto.getId().equals(message.getPayload().getId())) {
			dto = message.getPayload();
		}
	}

}
