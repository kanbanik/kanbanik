package com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.dto.ProjectDto;


public class ProjectDeletedMessage extends DefaultMessage<ProjectDto> {

	public ProjectDeletedMessage(ProjectDto payload, Object source) {
		super(payload, source);
	}

}
