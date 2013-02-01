package com.googlecode.kanbanik.client.modules;

import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.modules.security.UsersBox;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class SecurityModule extends SimplePanel implements KanbanikModule {

	private UsersBox usersBox = new UsersBox();
	
	@Override
	public void initialize(final ModuleInitializeCallback initializedCallback) {
		add(usersBox);
		
		ServerCommandInvokerManager.getInvoker().<VoidParams, SimpleParams<ListDto<UserDto>>> invokeCommand(
				ServerCommand.GET_ALL_USERS_COMMAND,
				new VoidParams(),
				new BaseAsyncCallback<SimpleParams<ListDto<UserDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<UserDto>> result) {
						usersBox.setUsers(result.getPayload().getList());
						initializedCallback.initialized(SecurityModule.this);
					}
				});
		
		setVisible(true);		
	}

}
