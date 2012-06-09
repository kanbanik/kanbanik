package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2.WorkflowEditingComponent.Position;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowEditingDropController extends FlowPanelDropController {
	private final WorkflowitemDto contextItem;
	
	private final WorkflowitemDto currentItem;
	
	private final Position position;

	public WorkflowEditingDropController(
			FlowPanel dropTarget,
			WorkflowitemDto contextItem,
			WorkflowitemDto currentItem, 
			Position position) {
		super(dropTarget);
		this.contextItem = contextItem;
		this.currentItem = currentItem;
		this.position = position;
	}
	
	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		Widget w = context.selectedWidgets.iterator().next();
		if (!(w instanceof WorkflowitemWidget)) {
			return;
		}
		WorkflowitemDto droppedItem = ((WorkflowitemWidget) w).getWorkflowitem();
		WorkflowitemDto nextItem = findNextItem();
		if (droppedItem.getId() != null && 
				nextItem.getId() != null && 
				droppedItem.getId().equals(nextItem.getId())) {
			// dropped before himself
			throw new VetoDragException();
		}
		super.onPreviewDrop(context);
	}
	
	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		
		if (context.selectedWidgets.size() > 1) {
			throw new UnsupportedOperationException("Only one workflowitem can be dragged at a time");
		}
		
		Widget w = context.selectedWidgets.iterator().next();
		if (!(w instanceof WorkflowitemWidget)) {
			return;
		}
		
		WorkflowitemDto droppedItem = ((WorkflowitemWidget) w).getWorkflowitem();
		WorkflowitemDto nextItem = findNextItem();
		
		droppedItem.setNextItem(nextItem);
		
		ServerCommandInvokerManager.getInvoker().<EditWorkflowParams, SimpleParams<WorkflowitemDto>> invokeCommand(
				ServerCommand.EDIT_WORKFLOW,
				new EditWorkflowParams(droppedItem, contextItem),
				new KanbanikAsyncCallback<SimpleParams<WorkflowitemDto>>() {

					@Override
					public void success(SimpleParams<WorkflowitemDto> result) {
						MessageBus.sendMessage(new RefreshBoardsRequestMessage("", this));
					}
				});
	}
	
	private WorkflowitemDto findNextItem() {
		if (position == Position.BEFORE) {
			return currentItem;
		} else if (position == Position.AFTER) {
			return currentItem.getNextItem();
		} else {
			// this can happen only if it has no children => has no next item
			return null;
		}
		
	}
}
