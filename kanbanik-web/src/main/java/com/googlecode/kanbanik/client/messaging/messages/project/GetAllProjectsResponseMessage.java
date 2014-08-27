package com.googlecode.kanbanik.client.messaging.messages.project;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;


public class GetAllProjectsResponseMessage extends BaseMessage<Dtos.BoardWithProjectsDto> {

	public GetAllProjectsResponseMessage(Dtos.BoardWithProjectsDto payload, Object source) {
		super(payload, source);
	}

}
