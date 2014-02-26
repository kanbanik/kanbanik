package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectEditedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskChangedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectEditingComponent;
import com.googlecode.kanbanik.dto.CommandNames;
import com.googlecode.kanbanik.client.api.ServerCallCallback;

import java.util.List;

import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskMovingDropController extends FlowPanelDropController {

	private Dtos.WorkflowitemDto workflowitem;
	private final Dtos.ProjectDto project;
	private final TaskContainer taskContainer;
	
	public TaskMovingDropController(TaskContainer dropTarget, Dtos.WorkflowitemDto workflowitem, Dtos.ProjectDto project) {
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
		final String prevWorkflowitem = task.getDto().getWorkflowitemId();
		final String prevProject = task.getDto().getProjectId();
		
		task.getDto().setWorkflowitemId(workflowitem.getId());
		task.getDto().setProjectId(project.getId());
		
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
        Dtos.MoveTaskDto moveTaskDto = DtoFactory.moveTaskDto();
        moveTaskDto.setCommandName(CommandNames.MOVE_TASK.name);
        moveTaskDto.setPrevOrder(prevOrder);
        moveTaskDto.setNextOrder(nextOrder);
        moveTaskDto.setTask(task.getDto());

        ServerCaller.<Dtos.MoveTaskDto, TaskDto>sendRequest(
                moveTaskDto,
                TaskDto.class,
                new ServerCallCallback<TaskDto>() {

                    @Override
                    public void success(TaskDto response) {
                        MessageBus.sendMessage(new TaskChangedMessage(response, TaskMovingDropController.this));
                    }

                    @Override
                    public void anyFailure() {
                        // reconstruct to the previous state (at least in memory)
                        // TODO move the item really back to its prev place
                        task.getDto().setWorkflowitemId(prevWorkflowitem);
                        task.getDto().setProjectId(prevProject);

                        MessageBus.sendMessage(new TaskChangedMessage(task.getDto(), TaskMovingDropController.this));
                    }
                }
        );

	}
}
