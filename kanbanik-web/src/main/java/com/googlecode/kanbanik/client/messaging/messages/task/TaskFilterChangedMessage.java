package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.components.filter.TaskFilter;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class TaskFilterChangedMessage extends BaseMessage<TaskFilter> {

    public TaskFilterChangedMessage(TaskFilter payload, Object source) {
        super(payload, source);
    }

}
