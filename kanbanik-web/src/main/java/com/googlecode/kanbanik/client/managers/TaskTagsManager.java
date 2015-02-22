package com.googlecode.kanbanik.client.managers;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;

import java.util.ArrayList;
import java.util.List;

public class TaskTagsManager implements MessageListener<Dtos.TaskDto> {

    private static final TaskTagsManager INSTANCE = new TaskTagsManager();

    private TagsChangedListener listener;

    private List<Dtos.TaskTag> tags;

    private static Dtos.TaskTag noTag;

    private TaskTagsManager() {
        MessageBus.registerListener(TaskAddedMessage.class, this);
    }

    public Dtos.TaskTag noTag() {
        if (noTag == null) {
            noTag = DtoFactory.taskTag();
            noTag.setName("No Tag");
            noTag.setColour("white");
        }

        return noTag;
    }

    public void addTaskTag(Dtos.TaskTag toAdd) {
        if (tags == null) {
            tags = new ArrayList<Dtos.TaskTag>();
        }

        boolean contains = false;

        for (Dtos.TaskTag tag : tags) {
            if (equals(tag, toAdd)) {
                contains = true;
                break;
            }
        }

        if (!contains) {
            tags.add(toAdd);
            if (listener != null) {
                listener.added(toAdd);
            }
        }
    }

    private boolean equals(Dtos.TaskTag tag, Dtos.TaskTag toAdd) {
        return objEq(tag.getName(), toAdd.getName());
    }

    private boolean objEq(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }

        if (o2 == null) {
            return false;
        }

        return o1.equals(o2);
    }

    public List<Dtos.TaskTag> getTags() {
        if (tags == null) {
            return new ArrayList<Dtos.TaskTag>();
        }

        return tags;
    }

    public static TaskTagsManager getInstance() {
        return INSTANCE;
    }


    @Override
    public void messageArrived(Message<Dtos.TaskDto> message) {
        if (message == null || message.getPayload() == null) {
            return;
        }

        List<Dtos.TaskTag> taskTags = message.getPayload().getTaskTags();
        if (taskTags != null) {
            for(Dtos.TaskTag tag : taskTags) {
                addTaskTag(tag);
            }
        }
    }

    public void setListener(TagsChangedListener listener) {
        this.listener = listener;
    }

    public static interface TagsChangedListener {
        void added(Dtos.TaskTag tag);
    }
}
