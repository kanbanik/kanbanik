package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.*;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.UserAddedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.List;

public class UserAddingComponent extends BaseUserManipulatingComponent {

	@UiField
	TextBox password2;
	
	interface MyUiBinder extends UiBinder<Widget, UserAddingComponent> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public UserAddingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	protected String validate() {
		
		String messageTexts = super.validate();
		
		if (isEmpty(password) || isEmpty(password2)) {
			messageTexts += "<li>The password fields must not be emtpy!";
		}
		
		messageTexts += checkPasswords(password, password2);
		
		return messageTexts;
	}

	@Override
	protected Dtos.UserManipulationDto createDto() {
        Dtos.UserManipulationDto dto = DtoFactory.userManipulationDto();
        dto.setUserName(username.getText());
        dto.setRealName(realName.getText());
        dto.setPictureUrl(pictureUrl.getText());
        dto.setVersion(1);
        dto.setPassword(password.getText());
        dto.setNewPassword(password.getText());

		dto.setPermissions(createPermissions());

        dto.setCommandName(CommandNames.CREATE_USER.name);
		return dto;
	}

    @Override
	public void cancelClicked(PanelContainingDialog dialog) {
		super.cancelClicked(dialog);
		
		clearAllFields();
	}

	private void clearAllFields() {
		username.setText("");
		realName.setText("");
		pictureUrl.setText("");
		password.setText("");
		password2.setText("");
	}
	
	@Override
	protected void makeServerCall() {

        ServerCaller.<Dtos.UserManipulationDto, Dtos.UserDto>sendRequest(
                createDto(),
                Dtos.UserDto.class,
                new ResourceClosingCallback<Dtos.UserDto>(this) {
                    @Override
                    public void success(Dtos.UserDto response) {
                        MessageBus.sendMessage(new UserAddedMessage(response, UserAddingComponent.this));
                        clearAllFields();
                    }
                }
        );

	}
	
}
