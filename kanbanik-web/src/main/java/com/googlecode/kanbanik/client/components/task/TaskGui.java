package com.googlecode.kanbanik.client.components.task;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
import com.googlecode.kanbanik.dto.ClassOfService;
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
	
	@UiField
	DivElement mainDiv;
	
	private TaskDto taskDto;
	
	private Map<ClassOfService, String> classOfServiceToCSS;
	
	interface MyUiBinder extends UiBinder<Widget, TaskGui> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	
	public TaskGui(TaskDto taskDto) {
		initWidget(uiBinder.createAndBindUi(this));
		
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		this.taskDto = taskDto;
		MessageBus.registerListener(TaskEditedMessage.class, this);
		MessageBus.registerListener(TaskChangedMessage.class, this);
		
		setupClassOfServiceToCSS();

		new TaskEditingComponent(this, editButton);
		new TaskDeletingComponent(this, deleteButton);
		
		setupAccordingDto(taskDto);
	}
	
	public void setupAccordingDto(TaskDto taskDto) {
		header.setStyleName(classOfServiceToStyle(taskDto));
		ticketIdLabel.setText(taskDto.getTicketId());
		nameLabel.setText(taskDto.getName());
	}

	private void setupClassOfServiceToCSS() {
		classOfServiceToCSS = new HashMap<ClassOfService, String>();
		classOfServiceToCSS.put(ClassOfService.EXPEDITE, "task-class-of-service-EXPEDITE");
		classOfServiceToCSS.put(ClassOfService.FIXED_DELIVERY_DATE, "task-class-of-service-FIXED_DELIVERY_DATE");
		classOfServiceToCSS.put(ClassOfService.INTANGIBLE, "task-class-of-service-STANDARD");
		classOfServiceToCSS.put(ClassOfService.STANDARD, "task-class-of-service-INTANGIBLE");
	}

	private String classOfServiceToStyle(TaskDto taskDto) {
		return classOfServiceToCSS.get(taskDto.getClassOfService());
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
