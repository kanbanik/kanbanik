package com.googlecode.kanbanik.dto;

public class UserDto implements KanbanikDto {
	
	private static final long serialVersionUID = -4004262630831771328L;

	private String userName;

	public UserDto() {
		
	}
	
	public UserDto(String userName) {
		super();
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
