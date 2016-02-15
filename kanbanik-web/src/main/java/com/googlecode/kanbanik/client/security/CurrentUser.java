package com.googlecode.kanbanik.client.security;

import com.google.gwt.storage.client.Storage;
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
    private static final String KANBANIK_SESSION_ID = "KanbanikSessionId";

    private String sessionId;

    private Dtos.UserDto user;

    private static final Storage storage = Storage.getLocalStorageIfSupported();

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
        removeSessionId();
		MessageBus.sendMessage(new LogoutEvent(user, this));
		
		this.user = null;
	}

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

    public boolean canMoveTask(String sourceBoardId, String destBoardId, String sourceProjectId, String destProjectId) {
        return containsIdOrAll(Dtos.PermissionTypes.MoveTask_b, sourceBoardId) &&
                containsIdOrAll(Dtos.PermissionTypes.MoveTask_b, destBoardId) &&
                containsIdOrAll(Dtos.PermissionTypes.MoveTask_p, sourceProjectId) &&
                containsIdOrAll(Dtos.PermissionTypes.MoveTask_p, destProjectId);

    }

    public boolean canAddTaskTo(Dtos.BoardDto board, Dtos.ProjectDto project) {
        return containsIdOrAll(Dtos.PermissionTypes.CreateTask_b, board.getId()) &&
                containsIdOrAll(Dtos.PermissionTypes.CreateTask_p, project.getId());
    }

    private boolean containsIdOrAll(Dtos.PermissionTypes type, String id) {
        for (Dtos.PermissionDto dto : getUser().getPermissions()) {
            if (dto.getPermissionType() == type.getValue()) {
                if (dto.getArgs().contains("*") || dto.getArgs().contains(id)) {
                    return true;
                }
            }
        }

        return false;
    }

	public boolean canSeeConfigure() {
        return containsOne(getPermissionTypes(),
                Dtos.PermissionTypes.CreateBoard.getValue(),
                Dtos.PermissionTypes.EditBoard.getValue(),
                Dtos.PermissionTypes.DeleteBoard.getValue(),

                Dtos.PermissionTypes.CreateProject.getValue(),
                Dtos.PermissionTypes.EditProject.getValue(),
                Dtos.PermissionTypes.DeleteProject.getValue()
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
        List<Dtos.PermissionDto> permissions = getUser().getPermissions();
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

    public String getSessionId() {
        if (storage != null) {
            return storage.getItem(KANBANIK_SESSION_ID);
        } else {
            return Cookies.getCookie(KANBANIK_SESSION_ID);
        }
    }

    public void setSessionId(String id) {
        if (storage != null) {
            storage.setItem(KANBANIK_SESSION_ID, id);
        } else {
            Cookies.setCookie(KANBANIK_SESSION_ID, id);
        }
    }

    public void removeSessionId() {
        if (storage != null) {
            storage.removeItem(KANBANIK_SESSION_ID);
        } else {
            Cookies.removeCookie(KANBANIK_SESSION_ID);
        }
    }

}
