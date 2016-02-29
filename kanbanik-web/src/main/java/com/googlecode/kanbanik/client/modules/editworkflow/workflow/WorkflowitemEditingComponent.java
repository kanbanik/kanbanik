package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
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

public class WorkflowitemEditingComponent implements PanelContainingDialolgListener, ClickHandler, MessageListener<Dtos.WorkflowitemDto>,ModulesLifecycleListener {

	private WorkflowItemEditPanel panel;

	private Dtos.WorkflowitemDto dto;
	
	private PanelContainingDialog dialog;
	
	public WorkflowitemEditingComponent(Dtos.WorkflowitemDto dto, HasClickHandlers clickHandlers) {
		super();
		this.dto = dto;
		clickHandlers.addClickHandler(this);

        new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);

		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	private WorkflowItemEditPanel toPanel() {
		WorkflowItemEditPanel panel = new WorkflowItemEditPanel();
		renderDto(panel);
		return panel;
	}

	private void renderDto(WorkflowItemEditPanel panel) {
		panel.setName(dto.getName());
		panel.setWipLimit(dto.getWipLimit());
		
		panel.setVerticalSizing(dto.getVerticalSize() != -1, dto.getVerticalSize());
		
		if (!dto.getNestedWorkflow().getWorkflowitems().isEmpty()) {
			panel.setVerticalSizingEnabled(false, "Can not be set for non-leaf workflowitems");
		}
		
		
		panel.setType(Dtos.ItemType.from(dto.getItemType()));
        panel.setupMessages();
	}
	
	private void flushDto() {
		dto.setName(panel.getName());
		dto.setWipLimit(panel.getWipLimit());
		dto.setItemType(panel.getItemType().getType());
		dto.setVerticalSize(panel.getVerticalSize());
	}

	public void onClick(ClickEvent event) {
		panel = toPanel();
		dialog = new PanelContainingDialog("Edit Workflow Item", panel, panel.getDefaultFocusWidget());
		dialog.addListener(this);
		dialog.center();
	}
	
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}

	public void okClicked(final PanelContainingDialog dialog) {
        if (!panel.validate()) {
            return;
        }

		flushDto();
        dto.setCommandName(CommandNames.EDIT_WORKFLOWITEM_DATA.name);
        dto.setSessionId(CurrentUser.getInstance().getSessionId());

        ServerCaller.<Dtos.WorkflowitemDto, Dtos.WorkflowitemDto>sendRequest(
                dto,
                Dtos.WorkflowitemDto.class,
                new ResourceClosingCallback<Dtos.WorkflowitemDto>(dialog) {

                    @Override
                    public void success(Dtos.WorkflowitemDto response) {
                        MessageBus.sendMessage(new WorkflowitemChangedMessage(response, this));
                        MessageBus.sendMessage(new BoardsRefreshRequestMessage(response.getParentWorkflow().getBoard(), this));
                    }
                }
        );
	}

	public void messageArrived(Message<Dtos.WorkflowitemDto> message) {
		if (dto.getId() != null && dto.getId().equals(message.getPayload().getId())) {
			dto = message.getPayload();
		}
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
}
