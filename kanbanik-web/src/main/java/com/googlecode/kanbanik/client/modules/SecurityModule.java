package com.googlecode.kanbanik.client.modules;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class SecurityModule extends Composite implements KanbanikModule {

	@UiField(provided=true)
	ListBoxWithAddEditDelete<UserDto> usersList;
	
	interface MyUiBinder extends UiBinder<Widget, SecurityModule> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public SecurityModule() {
		usersList = new ListBoxWithAddEditDelete<UserDto>(
				"Users",
				new IdProvider(),
				new LabelProvider(),
				new FakeComponent(),
				new FakeComponent(),
				new FakeComponent(),
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
	
	class FakeComponent implements Component<UserDto> {

		@Override
		public void setup(HasClickHandlers clickHandler, String title) {
			
		}

		@Override
		public void setDto(UserDto dto) {
			
		}
		
	}

}
