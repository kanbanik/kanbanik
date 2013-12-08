package com.googlecode.kanbanik.client.messaging.messages.user;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.UserDto;

public class UserAddedMessage extends BaseMessage<Dtos.UserDto> {

	public UserAddedMessage(Dtos.UserDto payload, Object source) {
		super(payload, source);
	}

}
