package com.googlecode.kanbanik.client.components.task;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.DeleteTasksRequestMessage;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskDeletingComponent implements ClickHandler {

	private TaskGui taskGui;
	
	public TaskDeletingComponent(TaskGui taskGui, HasClickHandlers clicklHandler) {
		super();
		this.taskGui = taskGui;
		clicklHandler.addClickHandler(this);
	}

	public void onClick(ClickEvent event) {
		if (taskGui.getDto().getId() == null) {
			return;
		}
		
		event.stopPropagation();
		event.preventDefault();
		
		MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
		MessageBus.sendMessage(ChangeTaskSelectionMessage.selectOne(taskGui.getDto(), this));
		
		List<TaskDto> list = new ArrayList<TaskDto>();
		list.add(taskGui.getDto());
		MessageBus.sendMessage(new DeleteTasksRequestMessage(list, this));
	}

}
