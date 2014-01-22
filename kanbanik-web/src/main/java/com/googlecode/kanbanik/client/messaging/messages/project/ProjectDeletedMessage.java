package com.googlecode.kanbanik.client.messaging.messages.project;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.client.api.Dtos.ProjectDto;

public class ProjectDeletedMessage extends BaseMessage<ProjectDto> {

	public ProjectDeletedMessage(ProjectDto payload, Object source) {
		super(payload, source);
	}

}
