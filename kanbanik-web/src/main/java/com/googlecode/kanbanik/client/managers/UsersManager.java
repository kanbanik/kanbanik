package com.googlecode.kanbanik.client.managers;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.Dtos;

import java.util.ArrayList;
import java.util.List;

public class UsersManager {

	private static final UsersManager INSTANCE = new UsersManager();

	private List<Dtos.UserDto> users;

	private static final Image defaultPicture = new Image(
			KanbanikResources.INSTANCE.noUserPicture());

	public static UsersManager getInstance() {
		return INSTANCE;
	}

	public void initUsers(List<Dtos.UserDto> users) {
		this.users = users;
	}

	public List<Dtos.UserDto> getUsers() {
		if (users == null) {
			return new ArrayList<Dtos.UserDto>();
		}
		return users;
	}

	public Image getPictureFor(Dtos.UserDto user) {
		if (user.getPictureUrl() == null) {
			return defaultPicture;
		}

		Image picture = new Image();
		picture.setVisible(false);
		picture.addLoadHandler(new PictureResizingLoadHandler(picture));
		picture.setUrl(user.getPictureUrl());
		Style style = picture.getElement().getStyle();
		style.setBorderStyle(BorderStyle.SOLID);
		style.setBorderWidth(1, Unit.PX);

		return picture;
	}

}