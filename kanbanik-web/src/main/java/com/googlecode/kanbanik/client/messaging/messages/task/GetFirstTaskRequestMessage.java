package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class GetFirstTaskRequestMessage extends BaseMessage<WorkflowitemDto> {

	public GetFirstTaskRequestMessage(WorkflowitemDto payload, Object source) {
		super(payload, source);
	}

}
