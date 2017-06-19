package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.workflowitem.WorkflowitemChangedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class WorkflowitemDeletingComponent implements ClickHandler, Closable, MessageListener<Dtos.WorkflowitemDto>,ModulesLifecycleListener {

	private PanelContainingDialog yesNoDialog;
	
	private VerticalPanel contentPanel;

	private Dtos.WorkflowitemDto dto;

	private CheckBox recursivelyCheckbox;
	
	public WorkflowitemDeletingComponent(Dtos.WorkflowitemDto dto, HasClickHandlers clickHandler) {
        this.dto = dto;
        clickHandler.addClickHandler(this);
        contentPanel = new VerticalPanel();
        recursivelyCheckbox = new CheckBox();

        HorizontalPanel warningPanel = new HorizontalPanel();
        warningPanel.add(new Label("Are you sure to delete this workflowitem '" + dto.getName() + "' ?"));
        warningPanel.setHeight("50px");

        HorizontalPanel recursivelyPanel = new HorizontalPanel();
        recursivelyPanel.add(new Label("Delete also tasks on this workflowitem?"));
        recursivelyPanel.add(recursivelyCheckbox);

        contentPanel.add(warningPanel);
        contentPanel.add(recursivelyPanel);

        new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	public void onClick(ClickEvent event) {
		if (dto.getId() == null) {
			return;
		}

		yesNoDialog = new PanelContainingDialog("Are you sure?", contentPanel);
		yesNoDialog.addListener(new YesNoDialogListener());
		yesNoDialog.center();
	}
	
	@Override
	public void close() {
		yesNoDialog.close();
	}

    @Override
    public void activated() {
        if (!MessageBus.listens(WorkflowitemChangedMessage.class, this)) {
            MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(WorkflowitemChangedMessage.class, this);
    }

    class YesNoDialogListener implements PanelContainingDialolgListener {
		
		public YesNoDialogListener() {
		}

		public void okClicked(PanelContainingDialog dialog) {
            Dtos.DeleteWorkflowitemDto deleteWorkflowitemDto = DtoFactory.deleteWorkflowitemDto(dto, recursivelyCheckbox.getValue());
            deleteWorkflowitemDto.setCommandName(CommandNames.DELETE_WORKFLOWITEM.name);
            deleteWorkflowitemDto.setSessionId(CurrentUser.getInstance().getSessionId());

            ServerCaller.<Dtos.DeleteWorkflowitemDto, Dtos.EmptyDto>sendRequest(
                    deleteWorkflowitemDto,
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
