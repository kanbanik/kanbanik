package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.BoardDto;


public class BoardChangedMessage extends BaseMessage<BoardDto> {

	public BoardChangedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
