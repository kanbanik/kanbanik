package com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.BoardDto;


public class BoardEditedMessage extends DefaultMessage<BoardDto> {

	public BoardEditedMessage(BoardDto payload, Object source) {
		super(payload, source);
	}

}
