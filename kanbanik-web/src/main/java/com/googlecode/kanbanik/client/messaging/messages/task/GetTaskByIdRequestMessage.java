package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;

import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class GetTaskByIdRequestMessage extends BaseMessage<String> {

	public GetTaskByIdRequestMessage(String id, Object source) {
		super(id, source);
	}

}
