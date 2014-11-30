package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskAddedMessage extends BaseMessage<TaskDto> {

    private boolean partOfMove;
    private boolean wasVisible;

    public TaskAddedMessage(TaskDto payload, Object source) {
		this(payload, source, false, false);
	}

    public TaskAddedMessage(TaskDto payload, Object source, boolean partOfMove, boolean wasVisible) {
        super(payload, source);
        this.partOfMove = partOfMove;
        this.wasVisible = wasVisible;
    }

    public boolean isPartOfMove() {
        return partOfMove;
    }

    public boolean wasVisible() {
        return wasVisible;
    }
}
