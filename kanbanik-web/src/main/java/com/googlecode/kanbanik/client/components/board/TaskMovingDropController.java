package com.googlecode.kanbanik.client.components.board;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskChangedMessage;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.MoveTaskParams;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class TaskMovingDropController extends FlowPanelDropController {

	private WorkflowitemDto workflowitem;
	private final ProjectDto project;
	private final TaskContainer taskContainer;
	
	public TaskMovingDropController(TaskContainer dropTarget, WorkflowitemDto workflowitem, ProjectDto project) {
		super(dropTarget.asFlowPanel());
		taskContainer = dropTarget;
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
		
		TaskDto prevTask = null;
		TaskDto nextTask = null;

		List<TaskDto> tasks = taskContainer.getTasks();
		
		int curIndex = taskContainer.getTaskIndex(task.getDto());
		if (curIndex != 0) {
			prevTask = tasks.get(curIndex - 1);
		}
		
		if (curIndex != tasks.size() - 1) {
			nextTask = tasks.get(curIndex + 1);
		}
		
		final String prevOrder = prevTask != null ? prevTask.getOrder() : null;
		final String nextOrder = nextTask != null ? nextTask.getOrder() : null;
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<MoveTaskParams, FailableResult<SimpleParams<TaskDto>>> invokeCommand(
				ServerCommand.MOVE_TASK,
				new MoveTaskParams(task.getDto(), project, prevOrder, nextOrder),
				new BaseAsyncCallback<FailableResult<SimpleParams<TaskDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						// reconstruct to the previous state (at least in memory)
						// TODO move the item really back to its prev place
						task.getDto().setWorkflowitem(prevWorkflowitem);
						task.getDto().setProject(prevProject);
						
						MessageBus.sendMessage(new TaskChangedMessage(task.getDto(), TaskMovingDropController.this));
					}

					@Override
					public void success(FailableResult<SimpleParams<TaskDto>> result) {
						super.success(result);
						
						MessageBus.sendMessage(new TaskChangedMessage(result.getPayload().getPayload(), TaskMovingDropController.this));
					}
					
					@Override
					public void failure(FailableResult<SimpleParams<TaskDto>> result) {
						super.failure(result);
						MessageBus.sendMessage(new TaskChangedMessage(result.getPayload().getPayload(), TaskMovingDropController.this));
					}
				});
		}});
		
	}
}
