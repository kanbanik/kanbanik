package com.googlecode.kanbanik.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.components.header.HeaderComponent;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.user.LoginEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.LogoutEvent;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.modules.LoginModule;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class KanbanikModuleManager {
	public void initialize() {

		registerListeners();
		
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				ServerCommandInvokerManager
						.getInvoker()
						.<VoidParams, FailableResult<SimpleParams<UserDto>>> invokeCommand(
								ServerCommand.GET_CURRENT_USER_COMMAND,
								new VoidParams(),
								new BaseAsyncCallback<FailableResult<SimpleParams<UserDto>>>() {

									@Override
									public void onSuccess(FailableResult<SimpleParams<UserDto>> result) {
										KanbanikProgressBar.hide();
										
										if (result.isSucceeded()) {
											// already logged in - happens for example during refresh of the browser
											autologin(result);
										} else {
											// the browser may think that he has a session even he does not - should never happen...
											autologout();
										}

									}

								});

			}
		});
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

	private void autologin(FailableResult<SimpleParams<UserDto>> result) {
		CurrentUser.getInstance().login(result.getPayload().getPayload());
	}
	
	private void autologout() {
		CurrentUser.getInstance().logout();
	}

	private void refreshListeners() {
		MessageBus.removeAllListeners();
		registerListeners();
	}
	
	private void registerListeners() {
		MessageBus.registerListener(LoginEvent.class, new LoginListener());
		MessageBus.registerListener(LogoutEvent.class, new LogoutListener());
	}

	class LoginListener implements MessageListener<UserDto> {

		@Override
		public void messageArrived(Message<UserDto> message) {
			showBoardsModule();
		}
		
	}
	
	class LogoutListener implements MessageListener<UserDto> {

		@Override
		public void messageArrived(Message<UserDto> message) {
			showLoginModule();
		}
		
	}	
	
	private void clearAllModules() {
		RootPanel.get("mainSection").clear();
	}

}
