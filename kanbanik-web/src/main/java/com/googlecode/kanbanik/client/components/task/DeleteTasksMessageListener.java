package com.googlecode.kanbanik.client.components.task;

import java.util.List;

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
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.DeleteTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class DeleteTasksMessageListener implements MessageListener<List<TaskDto>>, Closable {

	private PanelContainingDialog yesNoDialog;
	
	private WarningPanel warningPanel;
	
	public void initialize() {
		MessageBus.registerListener(DeleteTasksRequestMessage.class, this);
	}
	
	@Override
	public void messageArrived(Message<List<TaskDto>> message) {
		if (message.getPayload() == null || message.getPayload().size() == 0) {
			return;
		}
		
		visualizeYesNoDialog(message.getPayload());
	}
	
	private void visualizeYesNoDialog(List<TaskDto> selectedTasks) {
		// not using StringBuilder because that is extremly slow in JS
		String tasksIds = "[";
		for (int i = 0; i < selectedTasks.size(); i++) {
			TaskDto dto = selectedTasks.get(i);
			tasksIds += dto.getTicketId();
			if (i != selectedTasks.size() -1) {
				tasksIds += ", ";
			}
		}
		tasksIds += "]";
		DeleteKeyListener.INSTANCE.stop();
		
		warningPanel = new WarningPanel("Are you sure you want to delete the following tasks: '" + tasksIds + "'?");
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(selectedTasks));
		yesNoDialog.center();
	}
	
	class YesNoDialogListener implements PanelContainingDialolgListener {
		
		private List<TaskDto> tasksDto;
		
		public YesNoDialogListener(List<TaskDto> tasks) {
			this.tasksDto = tasks;
		}

		public void okClicked(PanelContainingDialog dialog) {
			DeleteKeyListener.INSTANCE.initialize();
			new KanbanikServerCaller(
					new Runnable() {

						public void run() {
							
							ListDto<TaskDto> list = new ListDto<TaskDto>();
							for(TaskDto task : tasksDto) {
								list.addItem(task);	
							}
							
							
							ServerCommandInvokerManager.getInvoker().<SimpleParams<ListDto<TaskDto>>, FailableResult<VoidParams>> invokeCommand(
									ServerCommand.DELETE_TASKS,
									new SimpleParams<ListDto<TaskDto>>(list),
									new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(DeleteTasksMessageListener.this) {

										@Override
										public void success(FailableResult<VoidParams> result) {
											for (TaskDto task : tasksDto) {
												MessageBus.sendMessage(new TaskDeletedMessage(task, DeleteTasksMessageListener.this));	
											}
										}
									});
											
						}
					}
			);
		}

		public void cancelClicked(PanelContainingDialog dialog) {
			MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
			DeleteKeyListener.INSTANCE.initialize();
		}
	}

	@Override
	public void close() {
		yesNoDialog.close();
	}

}
