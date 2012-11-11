package com.googlecode.kanbanik.client.components.task;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class TaskDeletingComponent implements ClickHandler, Closable {

	private TaskGui taskGui;

	private PanelContainingDialog yesNoDialog;
	
	private WarningPanel warningPanel;
	
	public TaskDeletingComponent(TaskGui taskGui, HasClickHandlers clicklHandler) {
		super();
		this.taskGui = taskGui;
		clicklHandler.addClickHandler(this);
		warningPanel = new WarningPanel("Are you sure you want to delete task with id + ' " + taskGui.getDto().getTicketId() + "'?");
	}

	public void onClick(ClickEvent event) {
		if (taskGui.getDto().getId() == null) {
			return;
		}
		
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(taskGui.getDto()));
		yesNoDialog.center();	
	}

	@Override
	public void close() {
		yesNoDialog.close();
	}
	
class YesNoDialogListener implements PanelContainingDialolgListener {
		
		private TaskDto taskDto;
		
		public YesNoDialogListener(TaskDto task) {
			this.taskDto = task;
		}

		public void okClicked(PanelContainingDialog dialog) {
			new KanbanikServerCaller(
					new Runnable() {

						public void run() {
							
							ServerCommandInvokerManager.getInvoker().<SimpleParams<TaskDto>, FailableResult<VoidParams>> invokeCommand(
									ServerCommand.DELETE_TASK,
									new SimpleParams<TaskDto>(taskDto),
									new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(TaskDeletingComponent.this) {

										@Override
										public void success(FailableResult<VoidParams> result) {
											MessageBus.sendMessage(new TaskDeletedMessage(taskDto, TaskDeletingComponent.this));	
										}
									});
											
						}
					}
			);
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

}
