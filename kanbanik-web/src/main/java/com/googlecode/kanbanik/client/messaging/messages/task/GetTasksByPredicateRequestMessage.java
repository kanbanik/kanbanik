package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.messaging.BaseMessage;

import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class GetTasksByPredicateRequestMessage extends BaseMessage<TaskDto> {

    private Predicate predicate;

	public GetTasksByPredicateRequestMessage(Predicate predicate, TaskDto payload, Object source) {
		super(payload, source);
        this.predicate = predicate;
	}

    public Predicate getPredicate() {
        return predicate;
    }

    public static interface Predicate {
        boolean match(TaskDto task);
    }
}
