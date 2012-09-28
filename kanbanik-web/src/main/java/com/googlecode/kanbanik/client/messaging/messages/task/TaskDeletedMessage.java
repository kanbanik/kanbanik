package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.TaskDto;


public class TaskDeletedMessage extends BaseMessage<TaskDto> {

	public TaskDeletedMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
