package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.BoardDTO;


public class BoardCreatedMessage extends DefaultMessage<BoardDTO> {

	public BoardCreatedMessage(BoardDTO payload, Object source) {
		super(payload, source);
	}

}
