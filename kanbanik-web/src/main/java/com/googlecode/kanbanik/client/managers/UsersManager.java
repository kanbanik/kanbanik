package com.googlecode.kanbanik.client.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.dto.UserDto;

public class UsersManager {
	
	private static final UsersManager INSTANCE = new UsersManager();

	private List<UserDto> users;
	
	private Map<UserDto, Image> pictures = new HashMap<UserDto, Image>();
	
	private Image defaultPicture = new Image(KanbanikResources.INSTANCE.noUserPicture());
	
	public static UsersManager getInstance() {
		return INSTANCE;
	}
	
	public void initUsers(List<UserDto> users) {
		this.users = users;
		
		initImages();
	}

	public List<UserDto> getUsers() {
		if (users == null) {
			return new ArrayList<UserDto>();
		}
		return users;
	}
	
	public Image getPictureFor(UserDto user) {
		if (pictures.containsKey(user)) {
			return pictures.get(user);
		}
		
		return defaultPicture;
	}
	
	private void initImages() {
		for (UserDto user : users) {
			if (user.getPictureUrl() == null) {
				continue;
			}
			
			Image picture = new Image();
			picture.setVisible(false);
			picture.addLoadHandler(new PictureResizingLoadHandler(picture));
			picture.setTitle(user.getRealName());
			picture.setUrl(user.getPictureUrl());
			pictures.put(user, picture);
		}
	}

}

class PictureResizingLoadHandler implements LoadHandler {

	private Image picture;
	
	private int expectedHeight = 40;
	
	public PictureResizingLoadHandler(Image picture) {
		this.picture = picture;
	}
	
	@Override
	public void onLoad(LoadEvent event) {
		int width = picture.getWidth();
		int height = picture.getHeight();
		
		if (width == 0 || height == 0) {
			return;
		}
		
		float ratio = height / width;
		int newWidth = Math.round(expectedHeight / ratio);
		picture.setHeight(expectedHeight + "px");
		picture.setWidth(newWidth + "px");
		picture.setVisible(true);
	}
	
}