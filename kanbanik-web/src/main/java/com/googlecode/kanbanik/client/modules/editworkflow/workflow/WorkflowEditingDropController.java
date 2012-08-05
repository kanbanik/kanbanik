package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WorkflowEditingComponent.Position;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowEditingDropController extends FlowPanelDropController {
	private final WorkflowitemDto contextItem;

	private final WorkflowitemDto currentItem;

	private final Position position;

	public WorkflowEditingDropController(FlowPanel dropTarget,
			WorkflowitemDto contextItem, WorkflowitemDto currentItem,
			Position position) {
		super(dropTarget);
		this.contextItem = contextItem;
		this.currentItem = currentItem;
		this.position = position;
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		// veto if dropped before or after himself

		Widget w = context.selectedWidgets.iterator().next();
		if (!(w instanceof WorkflowitemWidget)) {
			return;
		}
		WorkflowitemDto droppedItem = ((WorkflowitemWidget) w)
				.getWorkflowitem();
		if (droppedItem.getId() != null && currentItem != null && currentItem.getId() != null) {
			if (droppedItem.getId().equals(currentItem.getId())) {
				throw new VetoDragException();
			}

			WorkflowitemDto nextItem = findNextItem();
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

		final WorkflowitemDto droppedItem = ((WorkflowitemWidget) w)
				.getWorkflowitem();
		WorkflowitemDto nextItem = findNextItem();

		droppedItem.setNextItem(nextItem);

		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager
				.getInvoker()
				.<EditWorkflowParams, FailableResult<SimpleParams<WorkflowitemDto>>> invokeCommand(
						ServerCommand.EDIT_WORKFLOW,
						new EditWorkflowParams(droppedItem, contextItem),
						new KanbanikAsyncCallback<FailableResult<SimpleParams<WorkflowitemDto>>>() {

							@Override
							public void success(
									FailableResult<SimpleParams<WorkflowitemDto>> result) {
								if (!result.isSucceeded()) {
									new ErrorDialog(result.getMessage()).center();
								}
								MessageBus
										.sendMessage(new RefreshBoardsRequestMessage(
												"", this));
							}
						});
		}});
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
