package com.googlecode.kanbanik.client.modules;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikEntryPoint;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.LoginDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class LoginModule extends VerticalPanel {
	private TextBox name = new TextBox();

	private TextBox password = new TextBox();

	private PushButton button = new PushButton("login");

	private Label label = new Label("not logged in");

	public LoginModule(final KanbanikEntryPoint kanbanikEntryPoint) {
		add(name);
		add(password);
		add(button);
		add(label);
		
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				new KanbanikServerCaller(new Runnable() {

					public void run() {
						ServerCommandInvokerManager.getInvoker().<SimpleParams<LoginDto>, FailableResult<SimpleParams<UserDto>>> invokeCommand(
										ServerCommand.LOGIN_COMMAND,
										new SimpleParams<LoginDto>(new LoginDto(name.getText(), password.getText())),
										new BaseAsyncCallback<FailableResult<SimpleParams<UserDto>>>() {
											
								public void success(FailableResult<SimpleParams<UserDto>> result) {
											CurrentUser.getInstance().login(result.getPayload().getPayload());
											kanbanikEntryPoint.onModuleLoad();

											}
										});

					}
				});
			}

		});
	}
}
