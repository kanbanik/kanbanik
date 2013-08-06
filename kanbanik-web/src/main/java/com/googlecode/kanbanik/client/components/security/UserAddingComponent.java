package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.UserAddedMessage;
import com.googlecode.kanbanik.dto.ManipulateUserDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class UserAddingComponent extends BaseUserManipulatingComponent{

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
	protected ManipulateUserDto createDto() {
		return new ManipulateUserDto(
			      username.getText(),
			      realName.getText(),
			      pictureUrl.getText(),
			      1,
			      password.getText(),
			      password.getText());
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
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ManipulateUserDto>, FailableResult<SimpleParams<UserDto>>> invokeCommand(
				ServerCommand.CREATE_USER_COMMAND,
				new SimpleParams<ManipulateUserDto>(createDto()),
				new ResourceClosingAsyncCallback<FailableResult<SimpleParams<UserDto>>>(UserAddingComponent.this) {

					@Override
					public void success(FailableResult<SimpleParams<UserDto>> result) {
						MessageBus.sendMessage(new UserAddedMessage(result.getPayload().getPayload(), UserAddingComponent.this));
						clearAllFields();
					}
				});
		}

					});		
	}
	
}
