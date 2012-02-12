package com.googlecode.kanbanik.client.messaging;

public interface MessageListener<P> {
	void messageArrived(Message<P> message);
}
