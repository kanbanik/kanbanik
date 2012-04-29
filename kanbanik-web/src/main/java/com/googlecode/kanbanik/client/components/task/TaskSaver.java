package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.board.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class TaskSaver implements MessageListener<TaskDto> {

	private void saveTask(final TaskDto taskDTO) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						final boolean isNew = taskDTO.getId() == null;
						ServerCommandInvokerManager.getInvoker().<SimpleParams<TaskDto>, SimpleParams<TaskDto>> invokeCommand(
								ServerCommand.SAVE_TASK,
								new SimpleParams<TaskDto>(taskDTO),
								new KanbanikAsyncCallback<SimpleParams<TaskDto>>() {

									@Override
									public void success(SimpleParams<TaskDto> result) {
										if (isNew) {
											MessageBus.sendMessage(new TaskAddedMessage(result.getPayload(), TaskSaver.this));
										} else {
											MessageBus.sendMessage(new TaskEditSavedMessage(result.getPayload(), TaskSaver.this));
										}
									}
								});
						
					}
				}
		);
	}
	
	private void deleteTask(final TaskDto taskDto) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						
						ServerCommandInvokerManager.getInvoker().<SimpleParams<TaskDto>, VoidParams> invokeCommand(
								ServerCommand.DELETE_TASK,
								new SimpleParams<TaskDto>(taskDto),
								new KanbanikAsyncCallback<VoidParams>() {

									@Override
									public void success(VoidParams result) {
										MessageBus.sendMessage(new TaskDeletionSavedMessage(taskDto, TaskSaver.this));	
									}
								});
										
					}
				}
		);
	}

	public void messageArrived(Message<TaskDto> message) {
		if (message instanceof TaskChangedMessage) {
			saveTask(message.getPayload());	
		} 
		else if (message instanceof TaskDeleteRequestedMessage) {
			deleteTask(message.getPayload());
		}
		
	}
}
