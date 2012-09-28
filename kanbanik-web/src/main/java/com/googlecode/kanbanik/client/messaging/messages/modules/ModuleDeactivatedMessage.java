package com.googlecode.kanbanik.client.messaging.messages.modules;

import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class ModuleDeactivatedMessage extends BaseMessage<Class<?>> {

	public ModuleDeactivatedMessage(Class<?> payload, Object source) {
		super(payload, source);
	}

}
