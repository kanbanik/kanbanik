package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.BoardDto;


public class BoardCreatedMessage extends BaseMessage<BoardDto> {

	public BoardCreatedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
