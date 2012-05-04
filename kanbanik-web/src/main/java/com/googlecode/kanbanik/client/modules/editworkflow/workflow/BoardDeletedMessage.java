package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.BoardDto;


public class BoardDeletedMessage extends DefaultMessage<BoardDto> {

	public BoardDeletedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
