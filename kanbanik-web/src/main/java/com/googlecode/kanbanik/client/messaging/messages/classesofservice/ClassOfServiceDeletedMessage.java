package com.googlecode.kanbanik.client.messaging.messages.classesofservice;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServiceDeletedMessage extends BaseMessage<ClassOfServiceDto> {

	public ClassOfServiceDeletedMessage(ClassOfServiceDto payload, Object source) {
		super(payload, source);
	}

}
