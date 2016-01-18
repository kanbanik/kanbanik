package com.googlecode.kanbanik.client.managers;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsersManager implements MessageListener<Dtos.TaskDto> {

	private static final UsersManager INSTANCE = new UsersManager();

	private List<Dtos.UserDto> users;

    private static final Dtos.UserDto noUser = DtoFactory.userDto();

    private static final Dtos.UserDto allUsers = DtoFactory.userDto();

	private static final Image defaultPicture = new Image(
			KanbanikResources.INSTANCE.noUserPicture());

    private UserChangedListener listener;

    static {
        noUser.setUserName("No User");
        allUsers.setUserName("All Users");
    }

	private UsersManager() {
        MessageBus.registerListener(TaskAddedMessage.class, this);
        MessageBus.registerListener(TaskEditedMessage.class, this);

        MessageBus.registerListener(UserAddedMessage.class, new MessageListener<Dtos.UserDto>() {
            @Override
            public void messageArrived(Message<Dtos.UserDto> message) {
                addIfNew(message.getPayload());
            }
        });


        MessageBus.registerListener(UserDeletedMessage.class, new MessageListener<Dtos.UserDto>() {
            @Override
            public void messageArrived(Message<Dtos.UserDto> message) {
                Dtos.UserDto real = find(message.getPayload());
                if (real != null) {
                    users.remove(real);
                }
            }
        });

        MessageBus.registerListener(UserEditedMessage.class, new MessageListener<Dtos.UserDto>() {
            @Override
            public void messageArrived(Message<Dtos.UserDto> message) {
                Dtos.UserDto real = find(message.getPayload());
                if (real != null) {
                    users.remove(real);
                    users.add(message.getPayload());
                }
            }
        });
    }

	public static UsersManager getInstance() {
		return INSTANCE;
	}

	public void initUsers(List<Dtos.UserDto> users) {
		this.users = users;
	}

	public List<Dtos.UserDto> getUsers() {
		if (users == null) {
			return new ArrayList<Dtos.UserDto>();
		}

		Collections.sort(users, new Comparator<Dtos.UserDto>() {
			@Override
			public int compare(Dtos.UserDto userDto, Dtos.UserDto userDto2) {
				return userDto.getUserName().compareTo(userDto2.getUserName());
			}
		});
		return new ArrayList<>(users);
	}

    public Dtos.UserDto getNoUser() {
        return noUser;
    }

    public Dtos.UserDto getAllUsers() {
        return allUsers;
    }

    public Image getPictureFor(Dtos.UserDto user) {
		if (user.getPictureUrl() == null) {
			return defaultPicture;
		}

		final Image picture = new Image();
		picture.setVisible(false);
		picture.addLoadHandler(new PictureResizingLoadHandler(picture) {
			int expectedHeight = 40;
			@Override
			protected void doResize(int width, int height) {
				picture.setHeight(expectedHeight + "px");
			}
		});
		picture.setUrl(user.getPictureUrl());
		Style style = picture.getElement().getStyle();
		style.setBorderStyle(BorderStyle.SOLID);
		style.setBorderWidth(1, Unit.PX);
		style.setMarginTop(3, Unit.PX);
		style.setMarginRight(3, Unit.PX);
		style.setProperty("maxWidth", 43, Unit.PX);

		return picture;
	}

    public void setListener(UserChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void messageArrived(Message<Dtos.TaskDto> message) {
        if (message == null || message.getPayload() == null) {
            return;
        }

        Dtos.TaskDto task = message.getPayload();
        Dtos.UserDto assignee = task.getAssignee();
        if (assignee == null) {
            return;
        }

        String username = assignee.getUserName();
        if (username == null) {
            return;
        }

        addIfNew(assignee);
    }

    private void addIfNew(Dtos.UserDto candidate) {
        if (!contains(candidate)) {
            users.add(candidate);
            if (listener != null) {
                listener.added(candidate);
            }
        }
    }

    private boolean contains(Dtos.UserDto candidate) {
        return find(candidate) != null;
    }

    private Dtos.UserDto find(Dtos.UserDto candidate) {
        String candidateName = candidate.getUserName();
        for (Dtos.UserDto user : users) {
            if (candidateName.equals(user.getUserName())) {
                return user;
            }
        }

        return null;
    }

    public interface UserChangedListener {
        void added(Dtos.UserDto user);
    }

}