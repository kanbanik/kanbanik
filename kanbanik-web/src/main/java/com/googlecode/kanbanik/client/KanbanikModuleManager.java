package com.googlecode.kanbanik.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.header.HeaderComponent;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.user.LoginEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.LogoutEvent;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.modules.LoginModule;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class KanbanikModuleManager {
	
	private LoginListener loginListener = new LoginListener();
	
	private LogoutListener logoutListener = new LogoutListener();
	
	public void initialize() {

		registerListeners();
        String sessionId = CurrentUser.getInstance().getSessionId();
        if (sessionId == null || "".equals(sessionId)) {
            // the browser may think that he has a session even he does not - should never happen...
            autologout();
        } else {
            Dtos.SessionDto dto = DtoFactory.sessionDto(sessionId);
            dto.setCommandName(CommandNames.GET_CURRENT_USER.name);

            ServerCaller.<Dtos.SessionDto, Dtos.UserDto>sendRequest(
                    dto,
                    Dtos.UserDto.class,
                    new ServerCallCallback<Dtos.UserDto>() {

                        @Override
                        public void anyFailure() {
                            // the browser may think that he has a session even he does not - should never happen...
                            autologout();
                        }

                        @Override
                        public void success(Dtos.UserDto response) {
                            autologin(response);
                        }
                    }
            );
        }
	}

	private void showBoardsModule() {
		clearAllModules();
		refreshListeners();
		RootPanel.get("mainSection").add(new HeaderComponent());
		RootPanel.get("mainSection").add(new ControlPanelModule());
	}

	private void showLoginModule() {
		clearAllModules();
		refreshListeners();
		RootPanel.get("mainSection").add(new LoginModule());
	}

	private void autologin(Dtos.UserDto result) {
		CurrentUser.getInstance().login(result);
	}
	
	private void autologout() {
		CurrentUser.getInstance().logoutFrontend();
	}

	private void refreshListeners() {
//		registerListeners();
	}
	
	private void registerListeners() {
		MessageBus.registerListener(LoginEvent.class, loginListener);
		MessageBus.registerListener(LogoutEvent.class, logoutListener);
	}

	class LoginListener implements MessageListener<Dtos.UserDto> {

		public void messageArrived(Message<Dtos.UserDto> message) {
			showBoardsModule();
		}
		
	}
	
	class LogoutListener implements MessageListener<Dtos.UserDto> {

		public void messageArrived(Message<Dtos.UserDto> message) {
			showLoginModule();
		}
		
	}	
	
	private void clearAllModules() {
		RootPanel.get("mainSection").clear();
	}

}
