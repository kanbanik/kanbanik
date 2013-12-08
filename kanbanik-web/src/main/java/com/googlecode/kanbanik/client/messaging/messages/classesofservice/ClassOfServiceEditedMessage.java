package com.googlecode.kanbanik.client.messaging.messages.classesofservice;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class ClassOfServiceEditedMessage extends BaseMessage<Dtos.ClassOfServiceDto> {

	public ClassOfServiceEditedMessage(Dtos.ClassOfServiceDto payload, Object source) {
		super(payload, source);
	}

}
