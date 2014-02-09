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
import com.googlecode.kanbanik.client.messaging.messages.task.DeleteTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetSelectedTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetSelectedTasksRsponseMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

// listens on delete keyboard event and performs the tasks delete
public class DeleteKeyListener implements ModulesLifecycleListener, NativePreviewHandler {
	
	private boolean isBoards;
	
	private HandlerRegistration registration;
	
	public static final DeleteKeyListener INSTANCE = new DeleteKeyListener(); 
	
	private DeleteKeyListener() {
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
		GetSelectedTasksListener listener = new GetSelectedTasksListener();
		MessageBus.registerListener(GetSelectedTasksRsponseMessage.class, listener);
		MessageBus.sendMessage(new GetSelectedTasksRequestMessage(null, this));
		MessageBus.unregisterListener(GetSelectedTasksRsponseMessage.class, listener);

		List<TaskDto> selectedTasks = listener.getSelectedTasks();
		MessageBus.sendMessage(new DeleteTasksRequestMessage(selectedTasks, this));
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
		boolean keyDelete = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DELETE;
		
		if (down && keyDelete && isBoards) {
			doDelete();
		}
	}
	
}
