package com.googlecode.kanbanik.client.modules.security;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.dto.UserDto;

public class UsersBox extends Composite {

	@UiField
	ListBox boardsList;

	@UiField
	PushButton addButton;

	@UiField
	PushButton deleteButton;

	@UiField
	PushButton editButton;

	interface MyUiBinder extends UiBinder<Widget, UsersBox> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public UsersBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setUsers(List<UserDto> list) {
		for (UserDto user : list) {
			boardsList.addItem(user.getRealName() + "(" + user.getUserName() + ")");
		}
	}
	
}
