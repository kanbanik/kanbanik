package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;


public class WorkflowitemDeletedMessage extends DefaultMessage<WorkflowItemDTO> {

	public WorkflowitemDeletedMessage(WorkflowItemDTO payload, Object source) {
		super(payload, source);
	}

}
