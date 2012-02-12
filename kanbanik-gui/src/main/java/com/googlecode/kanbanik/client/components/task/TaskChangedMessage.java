package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.TaskDTO;


public class TaskChangedMessage extends DefaultMessage<TaskDTO> {

	public TaskChangedMessage(TaskDTO payload, Object source) {
		super(payload, source);
	}

}
