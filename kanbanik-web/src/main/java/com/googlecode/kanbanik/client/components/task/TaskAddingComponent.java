package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.shared.ClassOfServiceDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.TaskDTO;
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
	protected int getId() {
		return -1;
	}

	@Override
	protected String getClassOfService() {
		return ClassOfServiceDTO.STANDARD.toString();
	}

	@Override
	protected TaskDTO createBasicDTO() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setPlace(workflowItemDTO);
		taskDTO.setProject(projectDTO);
		return taskDTO;
	}
	
}
