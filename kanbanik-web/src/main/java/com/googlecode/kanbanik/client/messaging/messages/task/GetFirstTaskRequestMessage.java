package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class GetFirstTaskRequestMessage extends BaseMessage<Dtos.WorkflowitemDto> {

	public GetFirstTaskRequestMessage(Dtos.WorkflowitemDto payload, Object source) {
		super(payload, source);
	}

}
