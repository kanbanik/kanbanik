package com.googlecode.kanbanik.client.components.task;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.WarningPanel;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.GetSelectedTasksRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetSelectedTasksRsponseMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.TaskDto;


// listens on delete keyboard event and performs the tasks delete
public class TasksDeletingComponent implements ModulesLifecycleListener {
	
	private boolean isBoards;
	
	private PanelContainingDialog yesNoDialog;
	
	private WarningPanel warningPanel;
	
	public TasksDeletingComponent() {
		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
		isBoards = true;
	}
	
	public void initialize() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				boolean down = event.getTypeInt() == Event.ONKEYDOWN;
				boolean keyDelete = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DELETE;
				
				if (down && keyDelete && isBoards) {
					doDelete();
				}
			}
		});
	}
	
	private void doDelete() {
		GetSelectedTasksListener listener = new GetSelectedTasksListener();
		MessageBus.registerListener(GetSelectedTasksRsponseMessage.class, listener);
		MessageBus.sendMessage(new GetSelectedTasksRequestMessage(null, this));
		MessageBus.unregisterListener(GetSelectedTasksRsponseMessage.class, listener);

		List<TaskDto> selectedTasks = listener.getSelectedTasks();
		visualizeYesNoDialog(selectedTasks);
	}

	private void visualizeYesNoDialog(List<TaskDto> selectedTasks) {
		// not using StringBuilder because that is extremly slow in JS
		String tasksIds = "[";
		for (int i = 0; i < selectedTasks.size(); i++) {
			TaskDto dto = selectedTasks.get(i);
			tasksIds += dto.getTicketId();
			if (i != selectedTasks.size() -1) {
				tasksIds += ", ";
			}
		}
		tasksIds += "]";
		
		warningPanel = new WarningPanel("Are you sure you want to delete the following tasks: '" + tasksIds + "'?");
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(selectedTasks));
		yesNoDialog.center();
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
	
	class YesNoDialogListener implements PanelContainingDialolgListener {

		private List<TaskDto> dtos;
		
		public YesNoDialogListener(List<TaskDto> dtos) {
			this.dtos = dtos;
		}
		
		@Override
		public void okClicked(PanelContainingDialog dialog) {
			
		}

		@Override
		public void cancelClicked(PanelContainingDialog dialog) {
			
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
	
}
