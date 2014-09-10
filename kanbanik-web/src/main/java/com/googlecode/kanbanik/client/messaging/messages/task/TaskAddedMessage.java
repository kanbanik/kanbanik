package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskAddedMessage extends BaseMessage<TaskDto> {

    private boolean partOfMove;

	public TaskAddedMessage(TaskDto payload, Object source) {
		this(payload, source, false);
	}

    public TaskAddedMessage(TaskDto payload, Object source, boolean partOfMove) {
        super(payload, source);
        this.partOfMove = partOfMove;
    }

    public boolean isPartOfMove() {
        return partOfMove;
    }
}
