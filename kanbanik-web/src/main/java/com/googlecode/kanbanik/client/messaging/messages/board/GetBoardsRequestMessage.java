package com.googlecode.kanbanik.client.messaging.messages.board;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class GetBoardsRequestMessage extends BaseMessage<Dtos.BoardDto> {

	private Filter filter;

	public GetBoardsRequestMessage(Dtos.BoardDto payload, Filter filter, Object source) {
		super(payload, source);
	}

	public Filter getFilter() {
		return filter;
	}

	public interface Filter {
		boolean apply(Dtos.BoardDto boardDto);
	}
}
