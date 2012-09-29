package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.dto.ClassOfService;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;


public class TaskAddingComponent extends AbstractTaskEditingComponent {

	private final WorkflowitemDto inputQueue;
	
	private final ProjectDto project;

	public TaskAddingComponent(ProjectDto project, WorkflowitemDto inputQueue, HasClickHandlers clickHandler) {
		super(clickHandler);
		this.project = project;
		this.inputQueue = inputQueue;
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
		taskDTO.setProject(project);
		taskDTO.setWorkflowitem(inputQueue);
		return taskDTO;
	}

	@Override
	protected int getVersion() {
		return 0;
	}
	
}
