package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;


public class TaskDeletedMessage extends BaseMessage<TaskDto> {

    // indicates if this event has been sent as a part of a move operation or not
    private boolean partOfMove;

	public TaskDeletedMessage(TaskDto payload, Object source) {
		this(payload, source, false);
	}

    public TaskDeletedMessage(TaskDto payload, Object source, boolean partOfMove) {
        super(payload, source);
        this.partOfMove = partOfMove;
    }

    public boolean isPartOfMove() {
        return partOfMove;
    }

}
