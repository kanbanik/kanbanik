package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.ProjectDTO;


public class ProjectEditedMessage extends DefaultMessage<ProjectDTO> {

	public ProjectEditedMessage(ProjectDTO payload, Object source) {
		super(payload, source);
	}

}
