package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.BoardDto;

public class BoardRefreshedMessage extends DefaultMessage<BoardDto> {

	public BoardRefreshedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
