package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikProgressBar;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.workflowitem.WorkflowitemChangedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WorkflowEditingComponent.Position;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class WorkflowEditingDropController extends FlowPanelDropController implements MessageListener<Dtos.WorkflowitemDto> {
	
	private Dtos.WorkflowDto contextItem;

	private Dtos.WorkflowitemDto currentItem;

	private final Position position;

	public WorkflowEditingDropController(FlowPanel dropTarget,
			Dtos.WorkflowDto contextItem, Dtos.WorkflowitemDto currentItem,
			Position position) {
		super(dropTarget);
		this.contextItem = contextItem;
		this.currentItem = currentItem;
		this.position = position;
		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		// veto if dropped before or after himself

		Widget w = context.selectedWidgets.iterator().next();
		if (!(w instanceof WorkflowitemWidget)) {
			return;
		}
		Dtos.WorkflowitemDto droppedItem = ((WorkflowitemWidget) w).getWorkflowitem();
		if (droppedItem.getId() != null && currentItem != null && currentItem.getId() != null) {
			if (droppedItem.getId().equals(currentItem.getId())) {
				throw new VetoDragException();
			}

			Dtos.WorkflowitemDto nextItem = findNextItem();
			if (nextItem != null && nextItem.getId() != null
					&& droppedItem.getId().equals(nextItem.getId())) {
				throw new VetoDragException();
			}
		}

		super.onPreviewDrop(context);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		if (context.selectedWidgets.size() > 1) {
			throw new UnsupportedOperationException(
					"Only one workflowitem can be dragged at a time");
		}

		Widget w = context.selectedWidgets.iterator().next();
		if (!(w instanceof WorkflowitemWidget)) {
			return;
		}

		final Dtos.WorkflowitemDto droppedItem = ((WorkflowitemWidget) w)
				.getWorkflowitem();
		final Dtos.WorkflowitemDto nextItem = findNextItem();

        Dtos.EditWorkflowParams dto = DtoFactory.editWorkflowParams();
        dto.setCurrent(droppedItem);
        dto.setNext(nextItem);
        dto.setDestinationWorkflow(contextItem);
        dto.setBoard(contextItem.getBoard());


        ServerCaller.<Dtos.EditWorkflowParams, Dtos.WorkflowitemDto>sendRequest(
                dto,
                Dtos.WorkflowitemDto.class,
                new ServerCallCallback<Dtos.WorkflowitemDto>() {

                    @Override
                    public void success(Dtos.WorkflowitemDto response) {
                        MessageBus.sendMessage(new BoardsRefreshRequestMessage(contextItem.getBoard(), this));
                    }

                    @Override
                    public void onFailure(Dtos.ErrorDto errorDto) {
                        KanbanikProgressBar.hide();
                        new ErrorDialog(errorDto.getErrorMessage() + " Your change has been discarded and the workflow has been refreshed automatically.").center();
                        MessageBus.sendMessage(new BoardsRefreshRequestMessage(contextItem.getBoard(), this));
                    }
                }
        );
	}

	private Dtos.WorkflowitemDto findNextItem() {
		if (position == Position.BEFORE) {
			return currentItem;
		} else if (position == Position.AFTER) {
			List<Dtos.WorkflowitemDto> workflowitems = contextItem.getWorkflowitems() != null ? contextItem.getWorkflowitems() : new ArrayList<Dtos.WorkflowitemDto>();

			int index = workflowitems.indexOf(currentItem);
			boolean isLastItem = workflowitems.size() == index + 1;
			if (isLastItem) {
				return null;
			} else {
				return workflowitems.get(index + 1);
			}
		} else {
			// this can happen only if it has no children => has no next item
			return null;
		}

	}

	public void messageArrived(Message<Dtos.WorkflowitemDto> message) {
		// TODO ref - check if this is still needed
//		if (contextItem != null && message.getPayload().getId().equals(contextItem.getId())) {
//			contextItem = message.getPayload();
//		}
		
		if (currentItem != null && message.getPayload().getId().equals(currentItem.getId())) {
			currentItem = message.getPayload();
		}
	}
}
