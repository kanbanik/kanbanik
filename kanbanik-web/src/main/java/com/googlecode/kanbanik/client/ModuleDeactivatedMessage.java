package com.googlecode.kanbanik.client;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;

public class ModuleDeactivatedMessage extends DefaultMessage<Class<?>> {

	public ModuleDeactivatedMessage(Class<?> payload, Object source) {
		super(payload, source);
	}

}
