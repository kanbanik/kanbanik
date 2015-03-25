package com.googlecode.kanbanik.client.managers;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTagsManager {

    private static final TaskTagsManager INSTANCE = new TaskTagsManager();

    private TagsChangedListener listener;

    private List<Dtos.TaskTag> tags;

    private Map<String, List<String>> taskIdToTagName = new HashMap<String, List<String>>();

    private static Dtos.TaskTag noTag;

    private TaskTagsManager() {
        AddedOrEditedListener addedOrEditedListener = new AddedOrEditedListener();
        MessageBus.registerListener(TaskAddedMessage.class, addedOrEditedListener);
        MessageBus.registerListener(TaskEditedMessage.class, addedOrEditedListener);
        MessageBus.registerListener(TaskDeletedMessage.class, new RemovedListener());
    }

    public Dtos.TaskTag noTag() {
        if (noTag == null) {
            noTag = DtoFactory.taskTag();
            noTag.setName("No Tag");
            noTag.setColour("white");
        }

        return noTag;
    }

    private void addTaskTag(String taskId, Dtos.TaskTag toAdd) {
        if (tags == null) {
            tags = new ArrayList<Dtos.TaskTag>();
        }

        if (!taskIdToTagName.containsKey(taskId)) {
            taskIdToTagName.put(taskId, new ArrayList<String>());
        }

        taskIdToTagName.get(taskId).add(toAdd.getName());

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

    private void removeTaskTag(String tagOfTask) {
        Dtos.TaskTag tag = null;
        for (Dtos.TaskTag candidate : tags) {
            if (objEq(candidate.getName(), tagOfTask)) {
                tag = candidate;
                break;
            }
        }

        if (tag == null) {
            return;
        }

        tags.remove(tag);
        listener.removed(tag);
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


    class RemovedListener implements MessageListener<Dtos.TaskDto> {

        public void messageArrived(Message<Dtos.TaskDto> message) {
            if (message == null || message.getPayload() == null) {
                return;
            }

            List<Dtos.TaskTag> taskTags = message.getPayload().getTaskTags();
            String taskId = message.getPayload().getId();
            if (taskTags == null) {
                return;
            }

            if (!taskIdToTagName.containsKey(taskId)) {
                return;
            }

            List<String> tagNames = new ArrayList<String>();
            for(String name : taskIdToTagName.get(taskId)) {
                tagNames.add(name);
            }

            taskIdToTagName.remove(taskId);

            for (String tagToRemove : tagNames) {
                    // still some reference?
                    if (!isReferenced(tagToRemove)) {
                        // no, remove
                        removeTaskTag(tagToRemove);
                    }
                }
            }
        }

    class AddedOrEditedListener implements MessageListener<Dtos.TaskDto> {

        public void messageArrived(Message<Dtos.TaskDto> message) {
            if (message == null || message.getPayload() == null) {
                return;
            }

            List<Dtos.TaskTag> taskTags = message.getPayload().getTaskTags();
            String taskId = message.getPayload().getId();
            if (taskTags == null) {
                return;
            }

            List<String> tagNames = new ArrayList<String>();
            for(Dtos.TaskTag tag : taskTags) {
                addTaskTag(taskId, tag);
                tagNames.add(tag.getName());
            }

            // todo move it before the add
            if (!taskIdToTagName.containsKey(taskId)) {
                return;
            }

            for (String tagOfTask : new ArrayList<String>(taskIdToTagName.get(taskId))) {
                if (!tagNames.contains(tagOfTask)) {
                    taskIdToTagName.get(taskId).remove(tagOfTask);
                    // still some reference?
                    if (!isReferenced(tagOfTask)) {
                        // no, remove
                        removeTaskTag(tagOfTask);
                    }
                }
            }

        }
    }



    private boolean isReferenced(String tagOfTask) {
        for (List<String> vals : taskIdToTagName.values()) {
            if (vals.contains(tagOfTask)) {
                return true;
            }
        }

        return false;
    }

    public void setListener(TagsChangedListener listener) {
        this.listener = listener;
    }

    public interface TagsChangedListener {
        void added(Dtos.TaskTag tag);
        void removed(Dtos.TaskTag tag);
    }


}
