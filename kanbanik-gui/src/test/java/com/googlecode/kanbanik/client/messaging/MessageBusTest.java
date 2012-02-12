package com.googlecode.kanbanik.client.messaging;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MessageBusTest {
	
	private Listener1 listener1 = new Listener1();
	
	private Listener2 listener2 = new Listener2();
	
	@Test
	public void noListener() {
		MessageBus.sendMessage(new Message1("a", "b"));
	}
	
	@Test
	public void nullMessage() {
		MessageBus.sendMessage(null);
	}
	
	@Test
	public void oneListener() {
		MessageBus.registerListener(Message1.class, listener1);
		MessageBus.sendMessage(new Message1("a", "b"));
		assertThat(listener1.message.getPayload(), is(equalTo("a")));
	}
	
	@Test
	public void oneListenerNotCorrectMessage() {
		MessageBus.registerListener(Message2.class, listener1);
		MessageBus.sendMessage(new Message1("a", "b"));
		assertThat(listener1.message, is(nullValue()));
	}
	
	@Test
	public void twoMessages() {
		MessageBus.registerListener(Message1.class, listener1);
		MessageBus.registerListener(Message2.class, listener2);
		
		MessageBus.sendMessage(new Message1("a", "b"));
		MessageBus.sendMessage(new Message2("c", "d"));
		
		assertThat(listener1.message.getPayload(), is(equalTo("a")));
		assertThat(listener2.message.getPayload(), is(equalTo("c")));
	}
	
	@Test
	public void oneListenerOfTwiceSentMessage() {
		MessageBus.registerListener(Message1.class, listener1);
		
		MessageBus.sendMessage(new Message1("a", "b"));
		assertThat(listener1.message.getPayload(), is(equalTo("a")));
		
		MessageBus.sendMessage(new Message1("c", "d"));
		assertThat(listener1.message.getPayload(), is(equalTo("c")));
	}
	
	@Test
	public void oneListenerOfTwoDifferenMessages() {
		MessageBus.registerListener(Message1.class, listener1);
		MessageBus.registerListener(Message2.class, listener1);
		
		MessageBus.sendMessage(new Message1("a", "b"));
		assertThat(listener1.message.getPayload(), is(equalTo("a")));
		
		MessageBus.sendMessage(new Message2("c", "d"));
		assertThat(listener1.message.getPayload(), is(equalTo("c")));
	}
	
	@Test
	public void unregister() {
		MessageBus.registerListener(Message1.class, listener1);
		MessageBus.registerListener(Message1.class, listener2);
		MessageBus.unregisterListener(Message1.class, listener2);
		MessageBus.sendMessage(new Message1("a", "b"));
		assertThat(listener1.message.getPayload(), is(equalTo("a")));
		assertThat(listener2.message, is(nullValue()));
	}
	
	@Test
	public void listens() {
		MessageBus.registerListener(Message1.class, listener1);
		MessageBus.registerListener(Message2.class, listener1);
		
		assertThat(MessageBus.listens(Message1.class, listener1), is(true));
		assertThat(MessageBus.listens(Message1.class, listener2), is(false));
	}
	
	public void childOfMessage() {
		MessageBus.registerListener(Message2.class, listener1);
		MessageBus.sendMessage(new Message3("a", "b"));
		assertThat(listener1.message.getPayload(), is(equalTo("a")));
	}
}



class Message1 extends DefaultMessage<String> {

	public Message1(String payload, String source) {
		super(payload, source);
	}
	
}

class Message2 extends DefaultMessage<String> {

	public Message2(String payload, String source) {
		super(payload, source);
	}
	
}

class Message3 extends Message2 {

	public Message3(String payload, String source) {
		super(payload, source);
	}
	
}

class Listener1 implements MessageListener<String> {
	
	Message<String> message;
	
	public void messageArrived(Message<String> message) {
		this.message = message;
	}
	
}

class Listener2 implements MessageListener<String> {

	Message<String> message;
	
	public void messageArrived(Message<String> message) {
		this.message = message;
	}
	
}