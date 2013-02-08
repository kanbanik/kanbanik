package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.BoardDto;

public class BoardsRefreshRequestMessage extends BaseMessage<BoardDto> {

	public BoardsRefreshRequestMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
