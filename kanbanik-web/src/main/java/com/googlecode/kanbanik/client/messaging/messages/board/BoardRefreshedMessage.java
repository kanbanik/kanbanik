package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class BoardRefreshedMessage extends BaseMessage<Dtos.BoardDto> {

	public BoardRefreshedMessage(Dtos.BoardDto payload, Object source) {
		super(payload, source);
	}

}
