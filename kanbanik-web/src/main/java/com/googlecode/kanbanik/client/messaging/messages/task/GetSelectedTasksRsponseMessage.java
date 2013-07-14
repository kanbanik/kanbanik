package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.TaskDto;

public class GetSelectedTasksRsponseMessage extends BaseMessage<TaskDto> {

	public GetSelectedTasksRsponseMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
