package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
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
import com.googlecode.kanbanik.client.messaging.messages.classesofservice.ClassOfServiceDeletedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ClassOfServiceDeletingComponent implements Component<Dtos.ClassOfServiceDto>, ClickHandler, Closable {

	private Dtos.ClassOfServiceDto dto;

	private PanelContainingDialog yesNoDialog;

	private WarningPanel warningPanel;
	
	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(Dtos.ClassOfServiceDto dto) {
		this.dto = dto;
		warningPanel = new WarningPanel(
				"Are you sure you want to delete class of service'" + dto.getName() + "'?");
	}

	@Override
	public void close() {
		yesNoDialog.close();
	}

	@Override
	public void onClick(ClickEvent event) {
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(dto));
		yesNoDialog.center();
	}
	
	class YesNoDialogListener implements PanelContainingDialolgListener {

		private final Dtos.ClassOfServiceDto classOfServiceDto;

		public YesNoDialogListener(Dtos.ClassOfServiceDto classOfServiceDto) {
			this.classOfServiceDto = classOfServiceDto;
		}

		public void okClicked(PanelContainingDialog dialog) {
            classOfServiceDto.setCommandName(CommandNames.DELETE_CLASS_OF_SERVICE.name);
            classOfServiceDto.setSessionId(CurrentUser.getInstance().getSessionId());

            ServerCaller.<Dtos.ClassOfServiceDto, Dtos.EmptyDto>sendRequest(
                    classOfServiceDto,
                    Dtos.EmptyDto.class,
                    new ResourceClosingCallback<Dtos.EmptyDto>(yesNoDialog) {

                        @Override
                        public void success(Dtos.EmptyDto response) {
                            MessageBus.sendMessage(new ClassOfServiceDeletedMessage(classOfServiceDto, this));
                        }
                    }
            );
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

}