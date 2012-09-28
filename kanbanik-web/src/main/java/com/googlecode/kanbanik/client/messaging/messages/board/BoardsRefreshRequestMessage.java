package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class BoardsRefreshRequestMessage extends BaseMessage<String> {

	public BoardsRefreshRequestMessage(String payload, Object source) {
		super(payload, source);
	}

}
