package com.googlecode.kanbanik.client.security;

import com.google.gwt.user.client.Cookies;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.user.LoginEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.LogoutEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.UserDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public final class CurrentUser implements MessageListener<UserDto> {

    private static final CurrentUser instance = new CurrentUser();
    public static final String KANBANIK_SESSION_ID = "KanbanikSessionId";

    private String sessionId;

	private CurrentUser() {
	}

	public void logout() {
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				ServerCommandInvokerManager.getInvoker().<VoidParams, VoidParams> invokeCommand(
								ServerCommand.LOGOUT_COMMAND,
								new VoidParams(),
								new BaseAsyncCallback<VoidParams>() {
									
									public void success(VoidParams res) {
										CurrentUser.getInstance().logoutFrontend();
									}
								});

			}
		});
	}

	public void login(UserDto user) {
		this.user = user;
		registerListeners();
		MessageBus.sendMessage(new LoginEvent(user, this));
	}
	
	public void logoutFrontend() {
		unregisterListeners();
        Cookies.removeCookie(KANBANIK_SESSION_ID);
		MessageBus.sendMessage(new LogoutEvent(user, this));
		
		this.user = null;
	}
	
	private UserDto user; 
	
	
	private void unregisterListeners() {
		MessageBus.unregisterListener(UserEditedMessage.class, this);
		MessageBus.unregisterListener(UserDeletedMessage.class, this);
	}

	private void registerListeners() {
		MessageBus.registerListener(UserEditedMessage.class, this);
		MessageBus.registerListener(UserDeletedMessage.class, this);
	}
	
	public boolean isLoogedIn() {
		return this.user != null;
	}
	
	public static CurrentUser getInstance() {
		return instance;
	}
	
	public UserDto getUser() {
		return user;
	}

    public String getSessionId() {
        return Cookies.getCookie(KANBANIK_SESSION_ID);
    }

    public void setSessionId(String sessionId) {
        Cookies.setCookie(KANBANIK_SESSION_ID, sessionId);
    }

	@Override
	public void messageArrived(Message<UserDto> message) {
		if (!thisUserManipulated(message)) {
			return;
		}
		
		if (message instanceof UserEditedMessage) {
			user = message.getPayload();
		} else if (message instanceof UserDeletedMessage) {
			// current user deleted - we have to log him out
			logout();
		}
		
	}
	
	private boolean thisUserManipulated(Message<UserDto> message) {
		return message.getPayload().getUserName().equals(user.getUserName());
	}
}
