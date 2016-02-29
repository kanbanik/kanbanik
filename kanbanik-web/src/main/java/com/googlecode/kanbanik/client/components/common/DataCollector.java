package com.googlecode.kanbanik.client.components.common;

import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class DataCollector<T> implements MessageListener<T> {

    private List<T> data;

    public void messageArrived(Message<T> message) {
        data.add(message.getPayload());
    }

    public void init() {
        data = new ArrayList<>();
    }

    public List<T> getData() {
        return data;
    }
}
