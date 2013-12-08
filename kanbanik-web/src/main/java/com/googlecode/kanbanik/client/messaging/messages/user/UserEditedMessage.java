package com.googlecode.kanbanik.client.messaging.messages.user;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class UserEditedMessage extends BaseMessage<Dtos.UserDto> {

	public UserEditedMessage(Dtos.UserDto payload, Object source) {
		super(payload, source);
	}

}
