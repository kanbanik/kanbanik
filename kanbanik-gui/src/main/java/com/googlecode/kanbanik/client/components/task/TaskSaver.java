package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.services.KanbanikServiceAsync;
import com.googlecode.kanbanik.shared.TaskDTO;

public class TaskSaver implements MessageListener<TaskDTO> {

	private KanbanikServiceAsync kanbanikSerivece;

	public TaskSaver(KanbanikServiceAsync kanbanikSerivece) {
		this.kanbanikSerivece = kanbanikSerivece;
	}

	private void saveTask(final TaskDTO taskDTO) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						kanbanikSerivece.saveTask(taskDTO, new KanbanikAsyncCallback<TaskDTO>() {
							final boolean isNew = taskDTO.getId() == -1;
							
							@Override
							public void success(TaskDTO result) {
								if (isNew) {
									MessageBus.sendMessage(new TaskCreationSavedMessage(result, TaskSaver.this));
								} else {
									MessageBus.sendMessage(new TaskEditSavedMessage(result, TaskSaver.this));
								}
							}

						});				
					}
				}
		);
	}
	
	private void deleteTask(final TaskDTO taskDto) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						kanbanikSerivece.deleteTask(taskDto, new KanbanikAsyncCallback<Void>() {

							@Override
							public void success(Void result) {
								MessageBus.sendMessage(new TaskDeletionSavedMessage(taskDto, TaskSaver.this));
							}

						});				
					}
				}
		);
	}

	public void messageArrived(Message<TaskDTO> message) {
		if (message instanceof TaskChangedMessage) {
			saveTask(message.getPayload());	
		} else if (message instanceof TaskDeleteRequestedMessage) {
			deleteTask(message.getPayload());
		}
		
	}
}
