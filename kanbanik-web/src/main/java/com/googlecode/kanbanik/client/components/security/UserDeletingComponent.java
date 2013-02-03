package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.UserDeletedMessage;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class UserDeletingComponent implements ClickHandler, Closable,
		Component<UserDto> {

	private UserDto dto;

	private PanelContainingDialog yesNoDialog;

	private WarningPanel warningPanel;

	public UserDeletingComponent() {
		super();

	}

	public void onClick(ClickEvent event) {
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(dto));
		yesNoDialog.center();
	}

	@Override
	public void close() {
		yesNoDialog.close();
	}

	class YesNoDialogListener implements PanelContainingDialolgListener {

		private UserDto userDto;

		public YesNoDialogListener(UserDto userDto) {
			this.userDto = userDto;
		}

		public void okClicked(PanelContainingDialog dialog) {
			new KanbanikServerCaller(new Runnable() {

				public void run() {

					ServerCommandInvokerManager
							.getInvoker()
							.<SimpleParams<UserDto>, FailableResult<VoidParams>> invokeCommand(
									ServerCommand.DELETE_USER_COMMAND,
									new SimpleParams<UserDto>(userDto),
									new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(
											UserDeletingComponent.this) {

										@Override
										public void success(
												FailableResult<VoidParams> result) {
											MessageBus.sendMessage(new UserDeletedMessage(userDto, UserDeletingComponent.this));

										}
									});

				}
			});
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(UserDto dto) {
		this.dto = dto;
		warningPanel = new WarningPanel(
				"Are you sure you want to delete user '" + dto.getRealName()
						+ "' with username: '" + dto.getUserName() + "'?");
	}

}
