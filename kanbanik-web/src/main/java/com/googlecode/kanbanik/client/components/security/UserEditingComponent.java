package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.*;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

public class UserEditingComponent extends BaseUserManipulatingComponent {

	@UiField
	PasswordTextBox newPassword;
	
	@UiField
	PasswordTextBox newPassword2;
	
	@UiField
	CheckBox toChangePassword;
	
	interface MyUiBinder extends UiBinder<Widget, UserEditingComponent> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private Dtos.UserDto dto;
	
	public UserEditingComponent() {
		initWidget(uiBinder.createAndBindUi(this));

		toChangePassword.setValue(false);
		setChangePasswordEnabled(false);
		username.setEnabled(false);
		
		toChangePassword.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setChangePasswordEnabled(toChangePassword.getValue());
			}
		});
	}

	private void setChangePasswordEnabled(boolean enabled) {
		newPassword.setEnabled(enabled);
		newPassword2.setEnabled(enabled);
	}
	
	@Override
	protected Dtos.UserManipulationDto createDto() {
        Dtos.UserManipulationDto newDto = DtoFactory.userManipulationDto();
        newDto.setUserName(username.getText());
        newDto.setRealName(realName.getText());
        newDto.setPictureUrl(pictureUrl.getText());
        newDto.setVersion(dto.getVersion());
        newDto.setPassword(password.getText());
        newDto.setNewPassword(toChangePassword.getValue() ? newPassword.getText() : password.getText());
        newDto.setCommandName(CommandNames.EDIT_USER.name);
		newDto.setPermissions(createPermissions());

        return newDto;
	}
	
	@Override
	public void setDto(Dtos.UserDto dto) {
		super.setDto(dto);
		this.dto = dto;
		
		username.setText(dto.getUserName());
		realName.setText(dto.getRealName());
		pictureUrl.setText(dto.getPictureUrl());
	}
	
	@Override
	public void cancelClicked(PanelContainingDialog dialog) {
		super.cancelClicked(dialog);
		
		clearPasswordFields();
	}

	@Override
	protected void makeServerCall() {
        ServerCaller.<Dtos.UserManipulationDto, Dtos.UserDto>sendRequest(
                createDto(),
                Dtos.UserDto.class,
                new ResourceClosingCallback<Dtos.UserDto>(this) {
                    @Override
                    public void success(Dtos.UserDto response) {
                        MessageBus.sendMessage(new UserEditedMessage(response, UserEditingComponent.this));
                        setDto(response);

                        clearPasswordFields();
                    }
                }
        );
	}

	private void clearPasswordFields() {
		password.setText("");
		newPassword.setText("");
		newPassword2.setText("");
		toChangePassword.setValue(false);
		setChangePasswordEnabled(false);
	}

	@Override
	protected String validate() {
		String messages = super.validate();
		
		if (isEmpty(password)) {
			messages += "<li>You must provide the password!";
		}
		
		if (toChangePassword.getValue()) {
			messages += checkPasswords(newPassword, newPassword2);
		}
		
		return messages;
	}

}
