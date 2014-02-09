package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class GetFirstTaskResponseMessage extends BaseMessage<TaskDto> {

	public GetFirstTaskResponseMessage(TaskDto payload, Object source) {
		super(payload, source);
	}

}
