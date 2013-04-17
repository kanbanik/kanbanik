package com.googlecode.kanbanik.client.messaging.messages.classesofservice;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServiceAddedMessage extends BaseMessage<ClassOfServiceDto> {

	public ClassOfServiceAddedMessage(ClassOfServiceDto payload, Object source) {
		super(payload, source);
	}

}
