package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.user.UserDeletedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

public class UserDeletingComponent implements ClickHandler, Closable,
		Component<Dtos.UserDto> {

	private Dtos.UserDto dto;

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

		private Dtos.UserDto userDto;

		public YesNoDialogListener(Dtos.UserDto userDto) {
			this.userDto = userDto;
		}

		public void okClicked(PanelContainingDialog dialog) {
            userDto.setCommandName(CommandNames.DELETE_USER.name);
            ServerCaller.<Dtos.UserDto, Dtos.EmptyDto>sendRequest(
                    userDto,
                    Dtos.EmptyDto.class,
                    new ResourceClosingCallback<Dtos.EmptyDto>(UserDeletingComponent.this) {
                        @Override
                        public void success(Dtos.EmptyDto response) {
                            MessageBus.sendMessage(new UserDeletedMessage(userDto, UserDeletingComponent.this));
                        }
                    }
            );

		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(Dtos.UserDto dto) {
		this.dto = dto;
		warningPanel = new WarningPanel(
				"Are you sure you want to delete user '" + dto.getRealName()
						+ "' with username: '" + dto.getUserName() + "'?");
	}

}
