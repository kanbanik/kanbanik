package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.TaskDto;


public class TaskChangedMessage extends DefaultMessage<TaskDto> {

	public TaskChangedMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
