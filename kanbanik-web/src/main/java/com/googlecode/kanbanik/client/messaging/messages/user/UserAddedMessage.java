package com.googlecode.kanbanik.client.messaging.messages.user;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.UserDto;

public class UserAddedMessage extends BaseMessage<UserDto> {

	public UserAddedMessage(UserDto payload, Object source) {
		super(payload, source);
	}

}
