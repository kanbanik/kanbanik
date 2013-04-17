package com.googlecode.kanbanik.client.modules;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
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
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class SecurityModule extends Composite implements KanbanikModule, MessageListener<UserDto> {

	@UiField(provided=true)
	ListBoxWithAddEditDelete<UserDto> usersList;
	
	interface MyUiBinder extends UiBinder<Widget, SecurityModule> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public SecurityModule() {
		
		MessageBus.registerListener(UserAddedMessage.class, this);
		MessageBus.registerListener(UserEditedMessage.class, this);
		MessageBus.registerListener(UserDeletedMessage.class, this);
		
		usersList = new ListBoxWithAddEditDelete<UserDto>(
				"Users",
				new IdProvider(),
				new LabelProvider(),
				new UserAddingComponent(),
				new UserEditingComponent(),
				new UserDeletingComponent(),
				new Refresher()
				);
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void initialize(final ModuleInitializeCallback initializedCallback) {
		
		ServerCommandInvokerManager.getInvoker().<VoidParams, SimpleParams<ListDto<UserDto>>> invokeCommand(
				ServerCommand.GET_ALL_USERS_COMMAND,
				new VoidParams(),
				new BaseAsyncCallback<SimpleParams<ListDto<UserDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<UserDto>> result) {
						usersList.setContent(result.getPayload().getList());
						usersList.setSelectedDto(CurrentUser.getInstance().getUser());
						initializedCallback.initialized(SecurityModule.this);
					}
				});
		
		setVisible(true);		
	}
	
	class LabelProvider implements ListBoxWithAddEditDelete.LabelProvider<UserDto> {

		@Override
		public String getLabel(UserDto t) {
			return t.getRealName() + "(" + t.getUserName() + ")";
		}
		
	}
	
	class IdProvider implements ListBoxWithAddEditDelete.IdProvider<UserDto> {

		@Override
		public String getId(UserDto t) {
			return t.getUserName();
		}
	}
	
	class Refresher implements ListBoxWithAddEditDelete.Refresher<UserDto> {

		@Override
		public void refrehs(List<UserDto> items, UserDto newItem, int index) {
			items.set(index, newItem);
		}
	}

	@Override
	public void messageArrived(Message<UserDto> message) {
		if (message instanceof UserAddedMessage) {
			usersList.addNewItem(message.getPayload());
		} else if (message instanceof UserDeletedMessage) {
			usersList.removeItem(message.getPayload());
		} else if (message instanceof UserEditedMessage) {
			usersList.refresh(message.getPayload());
		}
	}

}
