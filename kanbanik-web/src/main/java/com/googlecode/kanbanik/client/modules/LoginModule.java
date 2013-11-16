package com.googlecode.kanbanik.client.modules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;
import com.googlecode.kanbanik.dto.UserDto;

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
		
		name.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
					doLogin();
				}
			}
		});
	}

	private void doLogin() {

        Dtos.LoginDto loginDto = DtoFactory.loginDto();
        loginDto.setUserName(name.getText());
        loginDto.setPassword(password.getText());
        loginDto.setCommandName(CommandNames.LOGIN.name);

        ServerCaller.<Dtos.LoginDto, Dtos.UserDto>sendRequest(
                loginDto,
                Dtos.UserDto.class,
                new ServerCallCallback<Dtos.UserDto>() {
                    @Override
                    public void anyFailure() {
                        password.setText("");
                    }

                    @Override
                    public void onSuccess(Dtos.UserDto response) {
                        CurrentUser.getInstance().login(
                                new UserDto(
                                        response.getUserName(),
                                        response.getRealName(),
                                        response.getPictureUrl(),
                                        response.getVersion()
                                ));
                    }
                }
        );
	}
}
