package com.googlecode.kanbanik.client.modules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.LoginDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class LoginModule extends Composite {
	
	@UiField
	TextBox name;

	@UiField
	TextBox password;

	@UiField
	PushButton loginButton = new PushButton("login");

	interface MyUiBinder extends UiBinder<Widget, LoginModule> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public LoginModule() {

		initWidget(uiBinder.createAndBindUi(this));

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				name.setFocus(true);				
			}
		});

		
		loginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doLogin();
			}
		});
		
		password.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
					doLogin();
				}
			}
		});
	}
	
	private void doLogin() {
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				ServerCommandInvokerManager
						.getInvoker()
						.<SimpleParams<LoginDto>, FailableResult<SimpleParams<UserDto>>> invokeCommand(
								ServerCommand.LOGIN_COMMAND,
								new SimpleParams<LoginDto>(
										new LoginDto(name.getText(),
												password.getText())),
								new BaseAsyncCallback<FailableResult<SimpleParams<UserDto>>>() {

									@Override
									public void failure(
											FailableResult<SimpleParams<UserDto>> result) {
										super.failure(result);
										
										password.setText("");
									}
									
									public void success(
											FailableResult<SimpleParams<UserDto>> result) {
										CurrentUser.getInstance()
												.login(result
														.getPayload()
														.getPayload());
									}
								});

			}
		});
	}
}
