package com.googlecode.kanbanik.client.components.task;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.DeleteTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetSelectedTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetTasksByPredicateRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetTasksRsponseMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

// listens on delete keyboard event and performs the tasks delete
public class GlobalKeyListener implements ModulesLifecycleListener, NativePreviewHandler {
	
	private boolean isBoards;
	
	private HandlerRegistration registration;
	
	public static final GlobalKeyListener INSTANCE = new GlobalKeyListener();
	
	private GlobalKeyListener() {
		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
		isBoards = true;
	}
	
	public void initialize() {
		if (registration == null) {
			registration = Event.addNativePreviewHandler(this);
		}
	}
	
	public void stop() {
		if (registration != null) {
			registration.removeHandler();
			registration = null;
		}
	}
	
	private void doDelete() {
		MessageBus.sendMessage(new DeleteTasksRequestMessage(getTasksByRequestMessage(new GetSelectedTasksRequestMessage(null, this)), this));
	}

    private void doSelect() {
        List<TaskDto> selectedTasks = getTasksByRequestMessage(new GetSelectedTasksRequestMessage(null, this));
        if (selectedTasks.size() == 0) {
            MessageBus.sendMessage(ChangeTaskSelectionMessage.selectAll(this));
        } else {
            selectSomeTasks(selectedTasks);
        }
    }

    private void selectSomeTasks(List<TaskDto> selectedTasks) {
        final List<String> workflowitemIds = new ArrayList<String>();
        for (TaskDto task : selectedTasks) {
            workflowitemIds.add(task.getWorkflowitemId());
        }

        GetTasksByPredicateRequestMessage requestMessage = new GetTasksByPredicateRequestMessage(new GetTasksByPredicateRequestMessage.Predicate() {
            @Override
            public boolean match(TaskDto task) {
                return workflowitemIds.contains(task.getWorkflowitemId());
            }
        }, null, this);

        List<TaskDto> tasks = getTasksByRequestMessage(requestMessage);

        MessageBus.sendMessage(ChangeTaskSelectionMessage.selectList(tasks, this));
    }

    private List<TaskDto> getTasksByRequestMessage(Message<TaskDto> requestMessage) {
        GetSelectedTasksListener listener = new GetSelectedTasksListener();
        MessageBus.registerListener(GetTasksRsponseMessage.class, listener);
        MessageBus.sendMessage(requestMessage);
        MessageBus.unregisterListener(GetTasksRsponseMessage.class, listener);

        return listener.getSelectedTasks();
    }

	class GetSelectedTasksListener implements MessageListener<TaskDto> {

		private List<TaskDto> selectedTasks = new ArrayList<TaskDto>();
		
		@Override
		public void messageArrived(Message<TaskDto> message) {
			selectedTasks.add(message.getPayload());
		}
		
		public List<TaskDto> getSelectedTasks() {
			return selectedTasks;
		}
		
	}
	
	@Override
	public void activated() {
		isBoards = true;
	}

	@Override
	public void deactivated() {
		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
		isBoards = false;
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		boolean down = event.getTypeInt() == Event.ONKEYDOWN;
        if (!isBoards || !down) {
            return;
        }

        boolean ctrlDown = event.getNativeEvent().getCtrlKey();
		boolean keyDelete = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DELETE;
        boolean keyA = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_A;

		if (keyDelete) {
			doDelete();
		} else if (ctrlDown && keyA) {
            doSelect();
            event.getNativeEvent().stopPropagation();
            event.getNativeEvent().preventDefault();
        }
	}

}
