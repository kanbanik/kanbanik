package com.googlecode.kanbanik.client.messaging.messages.project;

import com.googlecode.kanbanik.client.api.Dtos.ProjectDto;
import com.googlecode.kanbanik.client.messaging.BaseMessage;


public class GetAllProjectsRequestMessage extends BaseMessage<ProjectDto> {

	public GetAllProjectsRequestMessage(ProjectDto payload, Object source) {
		super(payload, source);
	}

}
