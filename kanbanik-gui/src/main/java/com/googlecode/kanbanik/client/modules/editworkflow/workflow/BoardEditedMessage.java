package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.BoardDTO;


public class BoardEditedMessage extends DefaultMessage<BoardDTO> {

	public BoardEditedMessage(BoardDTO payload, Object source) {
		super(payload, source);
	}

}
