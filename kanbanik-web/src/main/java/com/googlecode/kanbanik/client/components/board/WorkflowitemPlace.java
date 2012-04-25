package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.task.TaskDeleteRequestedMessage;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class WorkflowitemPlace extends Composite implements MessageListener<TaskDto> {

	@UiField
	Label stateName;

	@UiField
	Label wipLimit;

	@UiField(provided = true)
	Widget contentPanel;

	interface MyUiBinder extends UiBinder<Widget, WorkflowitemPlace> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final WorkflowitemDto workflowitemDto;

	private final DragController dragController;

	private final ProjectDto projectDto;

	public WorkflowitemPlace(WorkflowitemDto workflowitemDto,
			ProjectDto projectDto, Widget body, DragController dragController) {

		this.workflowitemDto = workflowitemDto;
		this.projectDto = projectDto;
		contentPanel = body;
		this.dragController = dragController;
		initWidget(uiBinder.createAndBindUi(this));

		stateName.setText(workflowitemDto.getName());
		wipLimit.setText(Integer.toString(workflowitemDto.getWipLimit()));

		MessageBus.registerListener(TaskAddedMessage.class, this);
		MessageBus.registerListener(TaskDeleteRequestedMessage.class, this);

	}

	public void messageArrived(Message<TaskDto> message) {

		if (!(contentPanel instanceof TaskContainer)) {
			return;
		}

		if (!isThisPlace(message.getPayload())) {
			return;
		}

		if (message instanceof TaskDeleteRequestedMessage) {
			((TaskContainer) contentPanel).removeTask(message.getPayload());
		} else if (message instanceof TaskAddedMessage) {
			TaskGui task = new TaskGui(message.getPayload());
			dragController.makeDraggable(task, task.getHeader());
			((TaskContainer) contentPanel).add(task);
		}

	}

	private boolean isThisPlace(TaskDto taskDto) {
		if (taskDto.getWorkflowitem() == null) {
			return false;
		}

		if (workflowitemDto.getId().equals(taskDto.getWorkflowitem().getId())) {
			if (taskDto.getProject() == null) {
				return false;
			}

			return projectDto.getId().equals(taskDto.getProject().getId());
		}

		return false;
	}
}
