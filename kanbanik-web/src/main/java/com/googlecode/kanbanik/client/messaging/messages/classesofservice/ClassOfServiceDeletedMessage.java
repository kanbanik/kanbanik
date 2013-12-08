package com.googlecode.kanbanik.client.messaging.messages.classesofservice;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class ClassOfServiceDeletedMessage extends BaseMessage<Dtos.ClassOfServiceDto> {

	public ClassOfServiceDeletedMessage(Dtos.ClassOfServiceDto payload, Object source) {
		super(payload, source);
	}

}
