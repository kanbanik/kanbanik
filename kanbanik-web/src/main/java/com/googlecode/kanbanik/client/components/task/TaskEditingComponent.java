package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.shared.ClassOfServiceDTO;
import com.googlecode.kanbanik.shared.TaskDTO;


public class TaskEditingComponent extends AbstractTaskEditingComponent {

	private TaskGui taskGui;
	
	public TaskEditingComponent(TaskGui taskGui, HasClickHandlers clickHandler) {
		super(clickHandler);
		this.taskGui = taskGui;
		initialize();
	}

	@Override
	protected String getTicketId() {
		return taskGui.getDto().getTicketId();
	}

	@Override
	protected String getTaskName() {
		return taskGui.getDto().getName();
	}

	@Override
	protected String getDescription() {
		return taskGui.getDto().getDescription();
	}

	@Override
	protected int getId() {
		return taskGui.getDto().getId();
	}
	
	@Override
	protected String getClassOfService() {
		ClassOfServiceDTO classOfService = taskGui.getDto().getClassOfService();
		if (classOfService == null) {
			return ClassOfServiceDTO.STANDARD.toString();
		}
		return classOfService.toString();
	}

	@Override
	protected TaskDTO createBasicDTO() {
		return taskGui.getDto();
	}

}
