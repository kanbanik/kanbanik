package com.googlecode.kanbanik.client.providers;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectAddedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.ProjectDeletedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.ProjectEditedMessage;
import com.googlecode.kanbanik.dto.ProjectDto;

public class ProjectDtoProvider implements MessageListener<ProjectDto> {
	
	private Map<String, ProjectDto> idToProject = new HashMap<String, ProjectDto>();
	
	public ProjectDto getDto(String id) {
		return idToProject.get(id);
	}

	public void messageArrived(Message<ProjectDto> message) {
		if (message instanceof ProjectDeletedMessage) {
			idToProject.remove(message.getPayload().getId());
		} else {
			idToProject.put(message.getPayload().getId(), message.getPayload());
		}
	}

	public void initialize() {
		MessageBus.registerListener(ProjectAddedMessage.class, this);
		MessageBus.registerListener(ProjectEditedMessage.class, this);
		MessageBus.registerListener(ProjectDeletedMessage.class, this);
	}
	
}
