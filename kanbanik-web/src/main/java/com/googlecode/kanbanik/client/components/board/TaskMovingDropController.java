package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.MoveTaskParams;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class TaskMovingDropController extends FlowPanelDropController {

	private WorkflowitemDto workflowitem;
	private final ProjectDto project;
	
	public TaskMovingDropController(FlowPanel dropTarget, WorkflowitemDto workflowitem, ProjectDto project) {
		super(dropTarget);
		this.workflowitem = workflowitem;
		this.project = project;
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
		final ProjectDto prevProject = task.getDto().getProject();
		
		task.getDto().setWorkflowitem(workflowitem);
		task.getDto().setProject(project);
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<MoveTaskParams, SimpleParams<TaskDto>> invokeCommand(
				ServerCommand.MOVE_TASK,
				new MoveTaskParams(task.getDto(), project),
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
						task.getDto().setProject(prevProject);
					}

				});
		}});
		
	}
}
