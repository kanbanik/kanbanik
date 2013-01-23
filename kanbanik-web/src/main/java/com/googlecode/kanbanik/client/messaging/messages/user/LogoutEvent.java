package com.googlecode.kanbanik.client.messaging.messages.user;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.UserDto;

public class LogoutEvent extends BaseMessage<UserDto> {

	public LogoutEvent(UserDto payload, Object source) {
		super(payload, source);
	}

}
