package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.MarkBoardsAsDirtyMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;

import java.util.List;

import static com.googlecode.kanbanik.client.api.Dtos.ClassOfServiceDto;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskEditingComponent extends AbstractTaskEditingComponent implements MessageListener<TaskDto> {

    private final TaskGui taskGui;
    private Panel taskChangedWarningPanel = new FlowPanel();

    private Panel taskDeletedWarningPanel = new FlowPanel();

    private Panel boardDirtydWarningPanel = new FlowPanel();

    private Panel taskDirtyWarningPanel = new FlowPanel();

    private BoardDirtyListener boardDirtyListener = new BoardDirtyListener();

    private TaskDto dto;

    private TaskDto modifiedDto;

    public TaskEditingComponent(TaskGui taskGui, HasClickHandlers clickHandler, Dtos.BoardDto boardDto) {
        super(clickHandler, boardDto);
        this.taskGui = taskGui;
    }

    @Override
    protected void onClicked() {
        dto = taskGui.getDto();
        MessageBus.registerListener(TaskEditedMessage.class, this);
        MessageBus.registerListener(TaskDeletedMessage.class, this);
        MessageBus.registerListener(TaskAddedMessage.class, this);
        MessageBus.registerListener(MarkBoardsAsDirtyMessage.class, boardDirtyListener);

        DeleteKeyListener.INSTANCE.stop();
        MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
        MessageBus.sendMessage(ChangeTaskSelectionMessage.selectOne(dto, TaskEditingComponent.this));
        doSetupAndShow();
    }

    @Override
    protected void onHideDialog() {
        super.onHideDialog();

        MessageBus.unregisterListener(TaskEditedMessage.class, this);
        MessageBus.unregisterListener(TaskDeletedMessage.class, this);
        MessageBus.unregisterListener(TaskAddedMessage.class, this);
        MessageBus.unregisterListener(BoardDirtyListener.class, boardDirtyListener);

    }

    @Override
    protected Panel initTaskDirtyWarningPanel() {
        taskChangedWarningPanel.add(red(new Label("This task has been modified by a different user - please select an action: ")));

        Button replaceThisDialog = new Button("Replace this dialog with the new task");
        Button replaceTask = new Button("Let this task replace the new task");
        taskChangedWarningPanel.add(replaceThisDialog);
        taskChangedWarningPanel.add(replaceTask);
        taskChangedWarningPanel.setVisible(false);

        taskDeletedWarningPanel.add(red(new Label("This task has been deleted by a different user - please select an action: ")));
        Button createNew = new Button("Make this task to be a new task");
        taskDeletedWarningPanel.add(createNew);
        taskDeletedWarningPanel.setVisible(false);

        boardDirtydWarningPanel.add(red(new Label("The board became dirty - the saving of this task may not pass")));
        boardDirtydWarningPanel.setVisible(false);

        taskDirtyWarningPanel.add(taskChangedWarningPanel);
        taskDirtyWarningPanel.add(taskDeletedWarningPanel);
        taskDirtyWarningPanel.add(boardDirtydWarningPanel);

        initButtonListeners(replaceThisDialog, replaceTask, createNew);

        return taskDirtyWarningPanel;
    }

    private IsWidget red(Label label) {
        label.getElement().getStyle().setColor("red");
        return label;
    }

    private void initButtonListeners(Button replaceThisDialog, Button replaceTask, Button createNew) {
        replaceThisDialog.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dto = modifiedDto;
                setupValues();
                taskChangedWarningPanel.setVisible(false);
           }
        });

        replaceTask.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // so on save this task will completely replace what has been saved before
                dto.setVersion(modifiedDto.getVersion());
                taskChangedWarningPanel.setVisible(false);
            }
        });

        createNew.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dto.setId(null);
                dto.setVersion(1);
                taskDeletedWarningPanel.setVisible(false);
            }
        });

    }

    @Override
    protected String getClassOfServiceAsString() {
        ClassOfServiceDto classOfService = dto.getClassOfService();
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
        return dto.getTicketId();
    }

    @Override
    protected String getTaskName() {
        return dto.getName();
    }

    @Override
    protected String getDescription() {
        return dto.getDescription();
    }

    @Override
    protected String getId() {
        return dto.getId();
    }

    @Override
    protected TaskDto createBasicDTO() {
        return dto;
    }

    @Override
    protected int getVersion() {
        return dto.getVersion();
    }

    @Override
    protected String getUser() {
        if (dto.getAssignee() == null) {
            return "";
        }

        return dto.getAssignee().getUserName();
    }

    @Override
    protected String getDueDate() {
        return dto.getDueDate();
    }

    @Override
    public void messageArrived(Message<TaskDto> message) {
        if (!dto.getId().equals(message.getPayload().getId())) {
            return;
        }

        if (message instanceof TaskDeletedMessage) {
            // only if it is not part of the move operation it means it has been really deleted
            if (!((TaskDeletedMessage) message).isPartOfMove()) {
                modifiedDto = message.getPayload();
                taskDeletedWarningPanel.setVisible(true);

                // no need - this will anyway not help
                taskChangedWarningPanel.setVisible(false);
            }
        } else if (message instanceof TaskEditedMessage) {
            modifiedDto = message.getPayload();
            taskChangedWarningPanel.setVisible(true);
        } else if (message instanceof TaskAddedMessage) {
            if (((TaskAddedMessage) message).isPartOfMove()) {
                // task has been moved - silently replace it's non editable parts so it can be saved
                dto.setVersion(message.getPayload().getVersion());
                dto.setWorkflowitemId(message.getPayload().getWorkflowitemId());
                dto.setProjectId(message.getPayload().getProjectId());
                dto.setBoardId(message.getPayload().getBoardId());
                dto.setOrder(message.getPayload().getOrder());
            }
        }
    }

    class BoardDirtyListener implements MessageListener<Dtos.BoardDto> {

        @Override
        public void messageArrived(Message<Dtos.BoardDto> message) {
            boardDirtydWarningPanel.setVisible(true);
        }
    }
}
