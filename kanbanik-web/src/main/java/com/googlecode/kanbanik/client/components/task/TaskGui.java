package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.client.providers.ClassOfServicesManager;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskGui extends Composite implements MessageListener<TaskDto> {
	
	@UiField
	FocusPanel header;

	@UiField
	Label ticketIdLabel;
	
	@UiField
	TextArea nameLabel;
	
	@UiField
	PushButton editButton;
	
	@UiField
	PushButton deleteButton;
	
	private TaskDto taskDto;
	
	interface MyUiBinder extends UiBinder<Widget, TaskGui> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	
	public TaskGui(TaskDto taskDto) {
		initWidget(uiBinder.createAndBindUi(this));
		
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		this.taskDto = taskDto;
		MessageBus.registerListener(TaskEditedMessage.class, this);
		MessageBus.registerListener(TaskChangedMessage.class, this);
		
		new TaskEditingComponent(this, editButton);
		new TaskDeletingComponent(this, deleteButton);
		
		setupAccordingDto(taskDto);
	}
	
	public void setupAccordingDto(TaskDto taskDto) {
		header.setStyleName("task-class-of-service");
		header.getElement().getStyle().setBackgroundColor(getColorOf(taskDto));
		ticketIdLabel.setText(taskDto.getTicketId());
		nameLabel.setText(taskDto.getName());
	}

	private String getColorOf(TaskDto taskDto) {
		if (taskDto.getClassOfService() == null) {
			return "#" + ClassOfServicesManager.getInstance().getDefaultClassOfService().getColour();
		}
		
		return "#" + taskDto.getClassOfService().getColour();
	}

	public FocusPanel getHeader() {
		return header;
	}

	public TaskDto getDto() {
		return taskDto;
	}

	public void messageArrived(Message<TaskDto> message) {
		TaskDto payload = message.getPayload();
		if (payload.getId().equals(taskDto.getId())) {
			this.taskDto = payload;
			setupAccordingDto(payload);
		}
	}
}
