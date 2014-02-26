package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.workflowitem.WorkflowitemChangedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class WorkflowitemDeletingComponent implements ClickHandler, Closable, MessageListener<Dtos.WorkflowitemDto> {

	private PanelContainingDialog yesNoDialog;
	
	private WarningPanel warningPanel;

	private Dtos.WorkflowitemDto dto;
	
	public WorkflowitemDeletingComponent(Dtos.WorkflowitemDto dto, HasClickHandlers clickHandler) {
		this.dto = dto;
		clickHandler.addClickHandler(this);
		warningPanel = new WarningPanel("Are you sure to delete this workflowitem '" + dto.getName() + "' ?");
		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	public void onClick(ClickEvent event) {
		if (dto.getId() == null) {
			return;
		}

		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener());
		yesNoDialog.center();
	}
	
	@Override
	public void close() {
		yesNoDialog.close();
	}

class YesNoDialogListener implements PanelContainingDialolgListener {
		
		public YesNoDialogListener() {
		}

		public void okClicked(PanelContainingDialog dialog) {

            dto.setCommandName(CommandNames.DELETE_WORKFLOWITEM.name);
            dto.setSessionId(CurrentUser.getInstance().getSessionId());

            ServerCaller.<Dtos.WorkflowitemDto, Dtos.EmptyDto>sendRequest(
                    dto,
                    Dtos.EmptyDto.class,
                    new ResourceClosingCallback<Dtos.EmptyDto>(dialog) {

                        @Override
                        public void success(Dtos.EmptyDto response) {
                            MessageBus.sendMessage(new BoardsRefreshRequestMessage(dto.getParentWorkflow().getBoard(), this));
                        }
                    }
            );
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

	public void messageArrived(Message<Dtos.WorkflowitemDto> message) {
		if (dto.getId() != null && dto.getId().equals(message.getPayload().getId())) {
			dto = message.getPayload();
		}
	}

}
