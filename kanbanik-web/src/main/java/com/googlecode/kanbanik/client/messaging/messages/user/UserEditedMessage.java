package com.googlecode.kanbanik.client.messaging.messages.user;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.UserDto;

public class UserEditedMessage extends BaseMessage<UserDto> {

	public UserEditedMessage(UserDto payload, Object source) {
		super(payload, source);
	}

}
