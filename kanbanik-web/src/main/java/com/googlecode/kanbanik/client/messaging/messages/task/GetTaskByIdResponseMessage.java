package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class GetTaskByIdResponseMessage extends BaseMessage<Dtos.TaskDto> {

    private boolean isVisible;

	public GetTaskByIdResponseMessage(Dtos.TaskDto task, boolean isVisible, Object source) {
		super(task, source);
        this.isVisible = isVisible;
	}

    public boolean isVisible() {
        return isVisible;
    }
}
