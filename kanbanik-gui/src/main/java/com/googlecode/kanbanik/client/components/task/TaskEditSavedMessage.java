package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.TaskDTO;


public class TaskEditSavedMessage extends DefaultMessage<TaskDTO> {

	public TaskEditSavedMessage(TaskDTO payload, Object source) {
		super(payload, source);
	}

}
