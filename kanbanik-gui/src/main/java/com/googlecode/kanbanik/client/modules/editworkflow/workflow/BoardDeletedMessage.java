package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.BoardDTO;


public class BoardDeletedMessage extends DefaultMessage<BoardDTO> {

	public BoardDeletedMessage(BoardDTO payload, Object source) {
		super(payload, source);
	}

}
