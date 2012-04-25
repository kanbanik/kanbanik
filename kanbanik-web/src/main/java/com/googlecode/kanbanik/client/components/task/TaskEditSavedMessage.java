package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.TaskDto;


public class TaskEditSavedMessage extends DefaultMessage<TaskDto> {

	public TaskEditSavedMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
