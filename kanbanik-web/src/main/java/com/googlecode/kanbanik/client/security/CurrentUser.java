package com.googlecode.kanbanik.client.security;

import com.googlecode.kanbanik.dto.UserDto;

public final class CurrentUser {
	
	private static final CurrentUser instance = new CurrentUser();
	
	private CurrentUser() {
		
	}
	
	private UserDto user; 
	
	public void login(UserDto user) {
		this.user = user;
	}
	
	public void logout() {
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
