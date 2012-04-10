package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import com.googlecode.kanbanik.client.messaging.DefaultMessage;
import com.googlecode.kanbanik.shared.ProjectDTO;


public class ProjectAddedMessage extends DefaultMessage<ProjectDTO> {

	public ProjectAddedMessage(ProjectDTO payload, Object source) {
		super(payload, source);
	}

}
