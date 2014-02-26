package com.googlecode.kanbanik.client.messaging.messages.user;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class UserAddedMessage extends BaseMessage<Dtos.UserDto> {

	public UserAddedMessage(Dtos.UserDto payload, Object source) {
		super(payload, source);
	}

}
