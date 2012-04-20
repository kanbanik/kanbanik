package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.MoveTaskParams;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class TaskMovingDropController extends FlowPanelDropController {

	private WorkflowitemDto workflowitem;
	
	public TaskMovingDropController(FlowPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		
		for (Widget widget : context.selectedWidgets) {
			if (widget instanceof TaskGui) {
				notifyDropped((TaskGui) widget);
			}
		}
	}

	private void notifyDropped(final TaskGui task) {
		final WorkflowitemDto prevWorkflowitem = task.getDto().getWorkflowitem();
		task.getDto().setWorkflowitem(workflowitem);
		
		ServerCommandInvokerManager.getInvoker().<MoveTaskParams, SimpleParams<TaskDto>> invokeCommand(
				ServerCommand.MOVE_TASK,
				new MoveTaskParams(task.getDto(), null),
				new KanbanikAsyncCallback<SimpleParams<TaskDto>>() {

					@Override
					public void success(SimpleParams<TaskDto> result) {
						// do nothing, the worflowitem has already been updated
					}
					
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						// reconstruct to the previous state (at least in memory)
						// TODO move the item really back to its prev place
						task.getDto().setWorkflowitem(prevWorkflowitem);
					}

				});
		
	}
}
