package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.dto.ClassOfService;
import com.googlecode.kanbanik.dto.TaskDto;


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
	protected String getId() {
		return taskGui.getDto().getId();
	}
	
	@Override
	protected String getClassOfServiceAsString() {
		ClassOfService classOfService = taskGui.getDto().getClassOfService();
		if (classOfService == null) {
			return ClassOfService.STANDARD.toString();
		}
		return classOfService.toString();
		
	}

	@Override
	protected TaskDto createBasicDTO() {
		return taskGui.getDto();
	}

}
