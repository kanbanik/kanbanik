package com.googlecode.kanbanik.client.security;

import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.LoginEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.LogoutEvent;
import com.googlecode.kanbanik.dto.UserDto;

public final class CurrentUser {
	
	private static final CurrentUser instance = new CurrentUser();
	
	private CurrentUser() {
		
	}
	
	private UserDto user; 
	
	public void login(UserDto user) {
		this.user = user;
		MessageBus.sendMessage(new LoginEvent(user, this));
	}
	
	public void logout() {
		MessageBus.sendMessage(new LogoutEvent(user, this));

		this.user = null;
	}
	
	public boolean isLoogedIn() {
		return this.user != null;
	}
	
	public static CurrentUser getInstance() {
		return instance;
	}
	
	public UserDto getUser() {
		return user;
	}
}
