package com.googlecode.kanbanik.dto;

public class UserDto implements KanbanikDto {
	
	private static final long serialVersionUID = -4004262630831771328L;

	private String userName;

	private String realName;
	
	private int version;
	
	public UserDto() {
		
	}
	
	public UserDto(String userName, String realName, int version) {
		super();
		this.userName = userName;
		this.realName = realName;
		this.version = version;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
