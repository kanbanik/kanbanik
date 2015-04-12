package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskChangedMessage extends BaseMessage<TaskDto> {

	private String newId = null;

	public TaskChangedMessage(TaskDto payload, Object source) {
		this(payload, null, source);
	}

	public TaskChangedMessage(TaskDto payload, String newId, Object source) {
		super(payload, source);

		this.newId = newId;
	}

	public String getNewId() {
		return newId;
	}
}
