package com.googlecode.kanbanik.client.components.board;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskAddedMessage extends DefaultMessage<TaskDto> {

	public TaskAddedMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
