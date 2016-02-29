package com.googlecode.kanbanik.client.components.task;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.DeleteTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.List;
import java.util.ArrayList;

import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;
import static com.googlecode.kanbanik.client.api.Dtos.TasksDto;

public class DeleteTasksMessageListener implements MessageListener<List<TaskDto>>, Closable {

    private PanelContainingDialog yesNoDialog;

    private WarningPanel warningPanel;

    public void initialize() {
        MessageBus.registerListener(DeleteTasksRequestMessage.class, this);
    }

    @Override
    public void messageArrived(Message<List<TaskDto>> message) {
        if (message.getPayload() == null || message.getPayload().isEmpty()) {
            return;
        }

        visualizeYesNoDialog(message.getPayload());
    }

    private void visualizeYesNoDialog(List<TaskDto> selectedTasks) {
        String tasksIds = "[";
        for (int i = 0; i < selectedTasks.size(); i++) {
            TaskDto dto = selectedTasks.get(i);
            tasksIds += dto.getTicketId();
            if (i != selectedTasks.size() - 1) {
                tasksIds += ", ";
            }
        }
        tasksIds += "]";
        GlobalKeyListener.INSTANCE.stop();

        warningPanel = new WarningPanel("Are you sure you want to delete the following tasks: '" + tasksIds + "'?");
        yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
        yesNoDialog.addListener(new YesNoDialogListener(selectedTasks));
        yesNoDialog.center();
    }

    class YesNoDialogListener implements PanelContainingDialolgListener {

        private List<TaskDto> tasksDto;

        public YesNoDialogListener(List<TaskDto> tasks) {
            this.tasksDto = tasks;
        }

        public void okClicked(PanelContainingDialog dialog) {
            GlobalKeyListener.INSTANCE.initialize();
            List<TaskDto> toSend = new ArrayList<>();
            for (TaskDto oneTask : tasksDto) {
                oneTask.setDescription("");
                toSend.add(oneTask);
            }
            TasksDto tasks = DtoFactory.tasksDto(toSend);
            tasks.setCommandName(CommandNames.DELETE_TASK.name);

            ServerCaller.<TasksDto, Dtos.EmptyDto>sendRequest(
                    tasks,
                    Dtos.EmptyDto.class,
                    new ResourceClosingCallback<Dtos.EmptyDto>(dialog) {

                        @Override
                        public void success(Dtos.EmptyDto response) {
                            for (TaskDto task : tasksDto) {
                                MessageBus.sendMessage(new TaskDeletedMessage(task, DeleteTasksMessageListener.this));
                            }
                        }
                    }
            );
        }

        public void cancelClicked(PanelContainingDialog dialog) {
            MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
            GlobalKeyListener.INSTANCE.initialize();
        }
    }

    @Override
    public void close() {
        yesNoDialog.close();
    }

}
