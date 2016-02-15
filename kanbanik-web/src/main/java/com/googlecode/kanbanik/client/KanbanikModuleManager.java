package com.googlecode.kanbanik.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.api.*;
import com.googlecode.kanbanik.client.components.header.HeaderComponent;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.modules.ModuleDeactivatedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.LoginEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.LogoutEvent;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.modules.LoginModule;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class KanbanikModuleManager {

    private LoginListener loginListener = new LoginListener();

    private LogoutListener logoutListener = new LogoutListener();

    private ServerEventsListener serverEventsListener = new ServerEventsListener();

    public void initialize() {
        registerListeners();
        String sessionId = CurrentUser.getInstance().getSessionId();

        loginOnBehalf(sessionId);

    }

    private void loginOnBehalf(final String sessionId) {
        Dtos.SessionDto dto = DtoFactory.sessionDto(sessionId);
        dto.setCommandName(CommandNames.GET_CURRENT_USER.name);

        ServerCaller.<Dtos.SessionDto, Dtos.UserDto>sendRequest(
                dto,
                Dtos.UserDto.class,
                new ServerCallCallback<Dtos.UserDto>() {

                    @Override
                    public void anyFailure() {
                        // exception on server - nothing better to do
                        autologout();
                    }

                    @Override
                    public void onUserNotLoggedIn(Dtos.ErrorDto errorDto) {
                        super.onUserNotLoggedIn(errorDto);
                        if (sessionId != null && !"".equals(sessionId)) {
                            // This can happen when the browser had a valid session but the server has been restarted and have forgotten it,
                            // Try to login with an empty session id
                            loginOnBehalf(null);
                        } else {
                            // ok, not even this worked
                            autologout();
                        }
                    }

                    @Override
                    public void success(Dtos.UserDto response) {
                        if (response == null || response.getPermissions() == null || response.getPermissions().size() == 0) {
                            autologout();
                        } else {
                            autologin(response);
                        }
                    }
                }
        );
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
            serverEventsListener.activate();
        }

    }

    class LogoutListener implements MessageListener<Dtos.UserDto> {

        public void messageArrived(Message<Dtos.UserDto> message) {
            showLoginModule();
            serverEventsListener.deactivate();
            for (Modules module : Modules.values()) {
                MessageBus.sendMessage(new ModuleDeactivatedMessage(module.toClass(), this));
            }

        }

    }

    private void clearAllModules() {
        RootPanel.get("mainSection").clear();
    }

}
