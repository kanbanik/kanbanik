package com.googlecode.kanbanik.client.messaging.messages.task;

import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.messaging.BaseMessage;

public class FilterChangedMessage extends BaseMessage<BoardsFilter> {

    public FilterChangedMessage(BoardsFilter payload, Object source) {
        super(payload, source);
    }

}
