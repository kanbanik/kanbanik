package com.googlecode.kanbanik.client.security;

import com.google.gwt.user.client.Cookies;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.user.LoginEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.LogoutEvent;
import com.googlecode.kanbanik.client.messaging.messages.user.UserDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.ArrayList;
import java.util.List;

public final class CurrentUser implements MessageListener<Dtos.UserDto> {

    private static final CurrentUser instance = new CurrentUser();
    public static final String KANBANIK_SESSION_ID = "KanbanikSessionId";

    private String sessionId;

	private CurrentUser() {
	}

	public void logout() {
        Dtos.SessionDto dto = DtoFactory.sessionDto(getSessionId());
        dto.setCommandName(CommandNames.LOGOUT.name);
        ServerCaller.<Dtos.SessionDto, Dtos.StatusDto>sendRequest(
                dto,
                Dtos.StatusDto.class,
                new ServerCallCallback<Dtos.StatusDto>() {
                    @Override
                    public void success(Dtos.StatusDto response) {
                        CurrentUser.getInstance().logoutFrontend();
                    }
                }
        );

	}

	public void login(Dtos.UserDto user) {
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
	
	private Dtos.UserDto user;
	
	
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
	
	public Dtos.UserDto getUser() {
		return user;
	}

    public String getSessionId() {
        return Cookies.getCookie(KANBANIK_SESSION_ID);
    }

    public void setSessionId(String sessionId) {
        Cookies.setCookie(KANBANIK_SESSION_ID, sessionId);
    }

	@Override
	public void messageArrived(Message<Dtos.UserDto> message) {
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
	
	private boolean thisUserManipulated(Message<Dtos.UserDto> message) {
		return message.getPayload().getUserName().equals(user.getUserName());
	}

	public boolean canSeeConfigure() {
        return containsOne(getPermissionTypes(),
                Dtos.PermissionTypes.ManipulateBoard.getValue(),
                Dtos.PermissionTypes.ManipulateProject.getValue()
        );
    }

    public boolean canSeeSecurity() {
        return containsOne(getPermissionTypes(),
                Dtos.PermissionTypes.EditUserData.getValue(),
                Dtos.PermissionTypes.EditUserPermissions.getValue(),
                Dtos.PermissionTypes.CreateUser.getValue(),
                Dtos.PermissionTypes.DeleteUser.getValue(),
                Dtos.PermissionTypes.CreateUser.getValue()
        );
    }

    private List<Integer> getPermissionTypes() {
        List<Dtos.PermissionDto> permissions = CurrentUser.getInstance().getUser().getPermissions();
        List<Integer> permissionTypes = new ArrayList<Integer>();
        for (Dtos.PermissionDto permission : permissions) {
            permissionTypes.add(permission.getPermissionType());
        }

        return permissionTypes;
    }

    private boolean containsOne(List<Integer> list, int... values) {
        for (int value : values) {
            if (list.contains(value)) {
                return true;
            }
        }

        return false;

    }
}
