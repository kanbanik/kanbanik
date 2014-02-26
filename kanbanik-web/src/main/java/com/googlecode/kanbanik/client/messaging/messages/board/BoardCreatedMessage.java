package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class BoardCreatedMessage extends BaseMessage<Dtos.BoardDto> {

	public BoardCreatedMessage(Dtos.BoardDto payload, Object source) {
		super(payload, source);
	}

}
