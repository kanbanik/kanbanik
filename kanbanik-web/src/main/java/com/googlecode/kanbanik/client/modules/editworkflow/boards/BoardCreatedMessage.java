package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.BoardDto;


public class BoardCreatedMessage extends DefaultMessage<BoardDto> {

	public BoardCreatedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
