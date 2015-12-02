package com.googlecode.kanbanik.client.modules;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.components.security.UserAddingComponent;
import com.googlecode.kanbanik.client.components.security.UserDeletingComponent;
import com.googlecode.kanbanik.client.components.security.UserEditingComponent;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.user.UserAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class SecurityModule extends Composite implements KanbanikModule, MessageListener<Dtos.UserDto>,ModulesLifecycleListener {

	@UiField(provided=true)
	ListBoxWithAddEditDelete<Dtos.UserDto> usersList;

    interface MyUiBinder extends UiBinder<Widget, SecurityModule> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public SecurityModule() {
        new ModulesLyfecycleListenerHandler(Modules.SECURITY_MODULE, this);

		MessageBus.registerListener(UserAddedMessage.class, this);
		MessageBus.registerListener(UserEditedMessage.class, this);
		MessageBus.registerListener(UserDeletedMessage.class, this);
		
		usersList = new ListBoxWithAddEditDelete<Dtos.UserDto>(
				"Users",
				new IdProvider(),
				new LabelProvider(),
				new UserAddingComponent(),
				new UserEditingComponent(),
				new UserDeletingComponent(),
				new Refresher()
				) {
            @Override
            protected boolean isDeleteEnabled(Dtos.UserDto selectedDto) {
                return super.isEnabled(selectedDto) && selectedDto.getUnlogged() != null && !selectedDto.getUnlogged();
            }
        };
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void initialize(final ModuleInitializeCallback initializedCallback) {
        Dtos.SessionDto dto = DtoFactory.sessionDto(CurrentUser.getInstance().getSessionId());
        dto.setCommandName(CommandNames.GET_ALL_USERS_COMMAND.name);
        ServerCaller.<Dtos.SessionDto, Dtos.UsersDto>sendRequest(
                dto,
                Dtos.UsersDto.class,
                new ServerCallCallback<Dtos.UsersDto>() {
					@Override
					public void success(Dtos.UsersDto response) {
						List<Dtos.UserDto> users = response.getValues();
						usersList.setContent(users);

						Dtos.UserDto currentUser = CurrentUser.getInstance().getUser();
						if (currentUser == null) {
							initializedCallback.initialized(SecurityModule.this);
						} else {
							for (Dtos.UserDto user : users) {
								if (user.getUserName().equals(currentUser.getUserName())) {
									usersList.setSelectedDto(currentUser);
									break;
								}
							}
							initializedCallback.initialized(SecurityModule.this);

						}
					}
				}
        );

		setVisible(true);		
	}
	
	class LabelProvider implements ListBoxWithAddEditDelete.LabelProvider<Dtos.UserDto> {

		@Override
		public String getLabel(Dtos.UserDto t) {
			return t.getRealName() + " (" + t.getUserName() + ")";
		}
		
	}
	
	class IdProvider implements ListBoxWithAddEditDelete.IdProvider<Dtos.UserDto> {

		@Override
		public String getId(Dtos.UserDto t) {
			return t.getUserName();
		}
	}
	
	class Refresher implements ListBoxWithAddEditDelete.Refresher<Dtos.UserDto> {

		@Override
		public void refrehs(List<Dtos.UserDto> items, Dtos.UserDto newItem, int index) {
			items.set(index, newItem);
		}
	}

    @Override
    public void activated() {
        if (!MessageBus.listens(UserAddedMessage.class, this)) {
            MessageBus.registerListener(UserAddedMessage.class, this);
        }

        if (!MessageBus.listens(UserEditedMessage.class, this)) {
            MessageBus.registerListener(UserEditedMessage.class, this);
        }

        if (!MessageBus.listens(UserDeletedMessage.class, this)) {
            MessageBus.registerListener(UserDeletedMessage.class, this);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(UserAddedMessage.class, this);
        MessageBus.unregisterListener(UserEditedMessage.class, this);
        MessageBus.unregisterListener(UserDeletedMessage.class, this);

        new ModulesLyfecycleListenerHandler(Modules.SECURITY_MODULE, this);
    }

	@Override
	public void messageArrived(Message<Dtos.UserDto> message) {
		if (message instanceof UserAddedMessage) {
			usersList.addNewItem(message.getPayload());
		} else if (message instanceof UserDeletedMessage) {
			usersList.removeItem(message.getPayload());
		} else if (message instanceof UserEditedMessage) {
			usersList.refresh(message.getPayload());
		}
	}

}
