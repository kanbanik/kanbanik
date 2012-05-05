package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.ProjectDto;


public class ProjectEditedMessage extends DefaultMessage<ProjectDto> {

	public ProjectEditedMessage(ProjectDto payload, Object source) {
		super(payload, source);
	}

}
