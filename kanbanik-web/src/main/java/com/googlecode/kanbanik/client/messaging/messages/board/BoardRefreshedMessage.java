package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.BoardDto;

public class BoardRefreshedMessage extends BaseMessage<BoardDto> {

	public BoardRefreshedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
