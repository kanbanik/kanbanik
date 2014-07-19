package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class GetTaskByIdResponseMessage extends BaseMessage<Dtos.TaskDto> {

	public GetTaskByIdResponseMessage(Dtos.TaskDto task, Object source) {
		super(task, source);
	}

}
