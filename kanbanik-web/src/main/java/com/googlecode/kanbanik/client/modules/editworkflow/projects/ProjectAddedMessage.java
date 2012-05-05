package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.ProjectDto;


public class ProjectAddedMessage extends DefaultMessage<ProjectDto> {

	public ProjectAddedMessage(ProjectDto payload, Object source) {
		super(payload, source);
	}

}
