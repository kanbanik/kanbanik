package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.TaskDto;


public class TaskDeletionSavedMessage extends DefaultMessage<TaskDto> {

	public TaskDeletionSavedMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
