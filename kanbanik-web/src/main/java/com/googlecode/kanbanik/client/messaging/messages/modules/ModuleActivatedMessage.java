package com.googlecode.kanbanik.client.messaging.messages.modules;

import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class ModuleActivatedMessage extends BaseMessage<Class<?>> {

	public ModuleActivatedMessage(Class<?> payload, Object source) {
		super(payload, source);
	}

}
