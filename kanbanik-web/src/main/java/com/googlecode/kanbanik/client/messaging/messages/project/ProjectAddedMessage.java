package com.googlecode.kanbanik.client.messaging.messages.project;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import com.googlecode.kanbanik.dto.ProjectDto;


public class ProjectAddedMessage extends BaseMessage<ProjectDto> {

	public ProjectAddedMessage(ProjectDto payload, Object source) {
		super(payload, source);
	}

}
