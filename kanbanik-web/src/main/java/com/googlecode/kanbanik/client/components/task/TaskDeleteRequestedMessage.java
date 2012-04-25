package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.TaskDto;


public class TaskDeleteRequestedMessage extends DefaultMessage<TaskDto> {

	public TaskDeleteRequestedMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
