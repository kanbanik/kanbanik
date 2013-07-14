package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.TaskDto;

// just formal TaskDto argument - it is not actually used
public class GetSelectedTasksRequestMessage extends BaseMessage<TaskDto> {

	public GetSelectedTasksRequestMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
