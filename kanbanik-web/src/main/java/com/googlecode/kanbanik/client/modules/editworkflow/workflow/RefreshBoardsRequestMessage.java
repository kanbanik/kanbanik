package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;

public class RefreshBoardsRequestMessage extends DefaultMessage<String> {

	public RefreshBoardsRequestMessage(String payload, Object source) {
		super(payload, source);
	}

}
