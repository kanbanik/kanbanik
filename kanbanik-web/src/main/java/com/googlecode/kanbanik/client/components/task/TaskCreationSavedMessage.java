package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.TaskDTO;


public class TaskCreationSavedMessage extends DefaultMessage<TaskDTO> {

	public TaskCreationSavedMessage(TaskDTO payload, Object source) {
		super(payload, source);
	}

}
