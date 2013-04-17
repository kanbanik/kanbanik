package com.googlecode.kanbanik.client.messaging.messages.classesofservice;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServiceEditedMessage extends BaseMessage<ClassOfServiceDto> {

	public ClassOfServiceEditedMessage(ClassOfServiceDto payload, Object source) {
		super(payload, source);
	}

}
