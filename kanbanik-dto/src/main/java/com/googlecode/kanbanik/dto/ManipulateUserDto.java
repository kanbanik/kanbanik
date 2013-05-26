package com.googlecode.kanbanik.dto;

/**
 * This class is never sent back to the client, but the client has to be able to
 * provide the password when create or manipulate the user.
 */
public class ManipulateUserDto extends UserDto {

	private static final long serialVersionUID = -7119347555119605998L;

	private String password;
	
	private String newPassword;

	public ManipulateUserDto() {
		super();
	}

	public ManipulateUserDto(String userName, String realName, String pictureUrl, int version,
			String password, String newPassword) {
		super(userName, realName, pictureUrl, version);
		this.password = password;
		this.newPassword = newPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
