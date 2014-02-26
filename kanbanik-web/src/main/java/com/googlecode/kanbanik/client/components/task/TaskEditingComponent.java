package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskChangedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.List;

import static com.googlecode.kanbanik.client.api.Dtos.ClassOfServiceDto;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskEditingComponent extends AbstractTaskEditingComponent {

    private TaskGui taskGui;

    public TaskEditingComponent(TaskGui taskGui, HasClickHandlers clickHandler, Dtos.BoardDto boardDto) {
        super(clickHandler, boardDto);
        this.taskGui = taskGui;
        initialize();
    }

    @Override
    protected void onClicked() {
        // retrieve the real task
        TaskDto task = taskGui.getDto();
        task.setCommandName(CommandNames.GET_TASK.name);
        task.setSessionId(CurrentUser.getInstance().getSessionId());
        ServerCaller.<TaskDto, TaskDto>sendRequest(
                task,
                TaskDto.class,
                new ServerCallCallback<TaskDto>() {

                    @Override
                    public void success(TaskDto response) {
                        MessageBus.sendMessage(new TaskChangedMessage(response, TaskEditingComponent.this));

                        DeleteKeyListener.INSTANCE.stop();
                        MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
                        MessageBus.sendMessage(ChangeTaskSelectionMessage.selectOne(response, TaskEditingComponent.this));

                        doSetupAndShow();
                    }
                }
        );
    }

    @Override
    protected String getClassOfServiceAsString() {
        ClassOfServiceDto classOfService = taskGui.getDto().getClassOfService();
        if (classOfService == null) {
            List<ClassOfServiceDto> classesOfService = ClassOfServicesManager.getInstance().getAll();
            if (classesOfService.size() != 0) {
                return classesOfService.iterator().next().getName();
            }

            return ClassOfServicesManager.getInstance().getDefaultClassOfService().getName();
        }

        return classOfService.getName();

    }

    @Override
    protected String getTicketId() {
        return taskGui.getDto().getTicketId();
    }

    @Override
    protected String getTaskName() {
        return taskGui.getDto().getName();
    }

    @Override
    protected String getDescription() {
        return taskGui.getDto().getDescription();
    }

    @Override
    protected String getId() {
        return taskGui.getDto().getId();
    }

    @Override
    protected TaskDto createBasicDTO() {
        return taskGui.getDto();
    }

    @Override
    protected int getVersion() {
        return taskGui.getDto().getVersion();
    }

    @Override
    protected String getUser() {
        if (taskGui.getDto().getAssignee() == null) {
            return "";
        }

        return taskGui.getDto().getAssignee().getUserName();
    }

    @Override
    protected String getDueDate() {
        return taskGui.getDto().getDueDate();
    }

}
