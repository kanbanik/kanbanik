package com.googlecode.kanbanik.client;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;

public class ModuleActivatedMessage extends DefaultMessage<Class<?>> {

	public ModuleActivatedMessage(Class<?> payload, Object source) {
		super(payload, source);
	}

}
