package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;
import com.googlecode.kanbanik.dto.ManipulateUserDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class UserEditingComponent extends BaseUserManipulatingComponent {

	@UiField
	PasswordTextBox newPassword;
	
	@UiField
	PasswordTextBox newPassword2;
	
	@UiField
	CheckBox toChangePassword;
	
	interface MyUiBinder extends UiBinder<Widget, UserEditingComponent> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private UserDto dto;
	
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
	protected ManipulateUserDto createDto() {
		return new ManipulateUserDto(
			      username.getText(),
			      realName.getText(),
			      pictureUrl.getText(),
			      dto.getVersion(),
			      password.getText(),
			      toChangePassword.getValue() ? newPassword.getText() : password.getText()
				);
	}
	
	@Override
	public void setDto(UserDto dto) {
		super.setDto(dto);
		this.dto = dto;
		
		username.setText(dto.getUserName());
		realName.setText(dto.getRealName());
		pictureUrl.setText(dto.getPictureUrl());
		
		postSetDto();
	}
	
	@Override
	public void cancelClicked(PanelContainingDialog dialog) {
		super.cancelClicked(dialog);
		
		clearPasswordFields();
	}

	@Override
	protected void makeServerCall() {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ManipulateUserDto>, FailableResult<SimpleParams<UserDto>>> invokeCommand(
				ServerCommand.EDIT_USER_COMMAND,
				new SimpleParams<ManipulateUserDto>(createDto()),
				new ResourceClosingAsyncCallback<FailableResult<SimpleParams<UserDto>>>(UserEditingComponent.this) {

					@Override
					public void success(FailableResult<SimpleParams<UserDto>> result) {
						MessageBus.sendMessage(new UserEditedMessage(result.getPayload().getPayload(), UserEditingComponent.this));
						setDto(result.getPayload().getPayload());
						
						clearPasswordFields();
					}

				});
		}

					});		
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
