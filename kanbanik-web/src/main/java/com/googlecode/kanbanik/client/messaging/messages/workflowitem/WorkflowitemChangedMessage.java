package com.googlecode.kanbanik.client.messaging.messages.workflowitem;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class WorkflowitemChangedMessage extends BaseMessage<Dtos.WorkflowitemDto> {

	public WorkflowitemChangedMessage(Dtos.WorkflowitemDto payload, Object source) {
		super(payload, source);
	}

}
