package com.googlecode.kanbanik.client.messaging.messages.workflowitem;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class WorkflowitemChangedMessage extends BaseMessage<WorkflowitemDto> {

	public WorkflowitemChangedMessage(WorkflowitemDto payload, Object source) {
		super(payload, source);
	}

}
