package com.googlecode.kanbanik.client.messaging;

public class BaseMessage<P> implements Message<P>{
	
	private P payload;
	
	private Object source;
	
	public BaseMessage(P payload, Object source) {
		super();
		this.payload = payload;
		this.source = source;
	}

	public P getPayload() {
		return payload;
	}
	
	public Object getSource() {
		return source;
	}
}
