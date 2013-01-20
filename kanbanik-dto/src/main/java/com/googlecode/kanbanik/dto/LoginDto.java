package com.googlecode.kanbanik.dto;

public class LoginDto implements KanbanikDto {

	private static final long serialVersionUID = 8755007615872715077L;

	private String userName;
	
	private String password;
	
	public LoginDto() {
		
	}
	
	public LoginDto(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
