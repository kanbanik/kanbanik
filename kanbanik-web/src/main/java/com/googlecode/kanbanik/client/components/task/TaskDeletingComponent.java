package com.googlecode.kanbanik.client.components.task;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskDeletingComponent implements ClickHandler {

	private TaskGui taskGui;

	private PanelContainingDialog yesNoDialog;
	
	private SimplePanel warningPanel = new SimplePanel();
	
	public TaskDeletingComponent(TaskGui taskGui, HasClickHandlers clicklHandler) {
		super();
		this.taskGui = taskGui;
		clicklHandler.addClickHandler(this);
		warningPanel.add(new Label("Are you sure you want to delete task with id + ' " + taskGui.getDto().getTicketId() + "'?"));
	}

	public void onClick(ClickEvent event) {
		if (taskGui.getDto().getId() == null) {
			return;
		}
		
		yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
		yesNoDialog.addListener(new YesNoDialogListener(taskGui.getDto()));
		yesNoDialog.center();	
	}
	
class YesNoDialogListener implements PanelContainingDialolgListener {
		
		private TaskDto taskDto;
		
		public YesNoDialogListener(TaskDto task) {
			this.taskDto = task;
		}

		public void okClicked(PanelContainingDialog dialog) {
//			MessageBus.sendMessage(new TaskDeleteRequestedMessage(taskDto, TaskDeletingComponent.class));
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

}
