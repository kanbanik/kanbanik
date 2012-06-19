package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowitemDeletingComponent implements ClickHandler {

	private PanelContainingDialog yesNoDialog;
	
	private HorizontalPanel warningPanel = new HorizontalPanel();

	private final WorkflowitemDto dto;
	
	public WorkflowitemDeletingComponent(WorkflowitemDto dto, HasClickHandlers clickHandler) {
		this.dto = dto;
		clickHandler.addClickHandler(this);
		warningPanel.add(new Label("Are you sure to delete this workflowitem '" + dto.getName() + "' ?"));
	}

	public void onClick(ClickEvent event) {
		if (dto.getId() == null) {
			return;
		}

		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener());
		yesNoDialog.center();
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
					new KanbanikAsyncCallback<FailableResult<VoidParams>>() {

						@Override
						public void success(FailableResult<VoidParams> result) {
							if (!result.isSucceeded()) {
								new ErrorDialog(result.getMessage()).center();
							} else {
								MessageBus.sendMessage(new RefreshBoardsRequestMessage("", this));
							}
						}
					});
			}});
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}
}
