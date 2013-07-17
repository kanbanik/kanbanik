package com.googlecode.kanbanik.client.messaging.messages.task;

import java.util.List;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.TaskDto;

public class DeleteTasksRequestMessage extends BaseMessage<List<TaskDto>> {

	public DeleteTasksRequestMessage(List<TaskDto> payload, Object source) {
		super(payload, source);
	}

}
