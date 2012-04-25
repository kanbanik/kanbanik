package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.dto.ClassOfService;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;


public class TaskAddingComponent extends AbstractTaskEditingComponent {

	private ProjectDTO projectDTO;
	
	private WorkflowItemPlaceDTO workflowItemDTO;
	
	public TaskAddingComponent(ProjectDTO projectDTO, WorkflowItemPlaceDTO workflowItemDTO, HasClickHandlers clickHandler) {
		super(clickHandler);
		this.projectDTO = projectDTO;
		this.workflowItemDTO = workflowItemDTO;
		initialize();
	}

	@Override
	protected String getTicketId() {
		return "";
	}

	@Override
	protected String getTaskName() {
		return "";
	}

	@Override
	protected String getDescription() {
		return "";
	}

	@Override
	protected String getId() {
		return null;
	}

	@Override
	protected String getClassOfServiceAsString() {
		return ClassOfService.STANDARD.toString();
	}

	@Override
	protected TaskDto createBasicDTO() {
		TaskDto taskDTO = new TaskDto();
		return taskDTO;
	}
	
}
