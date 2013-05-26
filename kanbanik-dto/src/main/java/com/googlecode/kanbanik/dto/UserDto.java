package com.googlecode.kanbanik.dto;

public class UserDto implements KanbanikDto {
	
	private static final long serialVersionUID = -4004262630831771328L;

	private String userName;

	private String realName;
	
	private String pictureUrl;
	
	private int version;
	
	public UserDto() {
		
	}
	
	public UserDto(String userName, String realName, String pictureUrl, int version) {
		super();
		this.userName = userName;
		this.realName = realName;
		this.pictureUrl = pictureUrl;
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

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	
}
