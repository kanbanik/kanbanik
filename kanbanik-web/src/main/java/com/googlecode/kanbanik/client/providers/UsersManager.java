package com.googlecode.kanbanik.client.providers;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.kanbanik.dto.UserDto;

public class UsersManager {
	
	private static final UsersManager INSTANCE = new UsersManager();

	private List<UserDto> users;
	
	public static UsersManager getInstance() {
		return INSTANCE;
	}
	
	public void setUsers(List<UserDto> users) {
		this.users = users;
	}
	
	public List<UserDto> getUsers() {
		if (users == null) {
			return new ArrayList<UserDto>();
		}
		return users;
	}
	
}
