package com.googlecode.kanbanik.client.messaging;

public interface Message<P> {
	P getPayload();
	Object getSource();
}
