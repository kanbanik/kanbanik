package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.ProjectDTO;


public class ProjectDeletedMessage extends DefaultMessage<ProjectDTO> {

	public ProjectDeletedMessage(ProjectDTO payload, Object source) {
		super(payload, source);
	}

}
