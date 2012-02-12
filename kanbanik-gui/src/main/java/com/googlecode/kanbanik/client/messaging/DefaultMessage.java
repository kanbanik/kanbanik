package com.googlecode.kanbanik.client.messaging;

public class DefaultMessage<P> implements Message<P>{
	
	private P payload;
	
	private Object source;
	
	public DefaultMessage(P payload, Object source) {
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
