package com.googlecode.kanbanik.client.model;

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
import com.googlecode.kanbanik.client.components.task.TaskDeletingComponent;
import com.googlecode.kanbanik.client.components.task.TaskEditSavedMessage;
import com.googlecode.kanbanik.client.components.task.TaskEditingComponent;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.shared.ClassOfServiceDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public class TaskGui extends Composite implements MessageListener<TaskDTO> {

	private WorkflowItemGUI workflowItem;
	
	private ProjectGUI project;

	private TaskDTO taskDTO;
	
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
	
	private Map<ClassOfServiceDTO, String> classOfServiceToCSS;
	
	interface MyUiBinder extends UiBinder<Widget, TaskGui> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	
	public TaskGui(TaskDTO taskDTO) {
		initWidget(uiBinder.createAndBindUi(this));
		
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		this.taskDTO = taskDTO;
		MessageBus.registerListener(TaskEditSavedMessage.class, this);
		
		setupClassOfServiceToCSS();

		new TaskEditingComponent(this, editButton);
		new TaskDeletingComponent(this, deleteButton);
		
		setupAccordingDTO(taskDTO);
	}
	
	public void setupAccordingDTO(TaskDTO taskDTO) {
		header.setStyleName(classOfServiceToStyle(taskDTO));
		ticketIdLabel.setText(taskDTO.getTicketId());
		nameLabel.setText(taskDTO.getName());
	}

	private void setupClassOfServiceToCSS() {
		classOfServiceToCSS = new HashMap<ClassOfServiceDTO, String>();
		classOfServiceToCSS.put(ClassOfServiceDTO.EXPEDITE, "task-class-of-service-EXPEDITE");
		classOfServiceToCSS.put(ClassOfServiceDTO.FIXED_DELIVERY_DATE, "task-class-of-service-FIXED_DELIVERY_DATE");
		classOfServiceToCSS.put(ClassOfServiceDTO.INTANGIBLE, "task-class-of-service-STANDARD");
		classOfServiceToCSS.put(ClassOfServiceDTO.STANDARD, "task-class-of-service-INTANGIBLE");
	}

	private String classOfServiceToStyle(TaskDTO taskDTO) {
		return classOfServiceToCSS.get(taskDTO.getClassOfService());
	}

	public void taskMoved(WorkflowItemGUI toItem) {
		if (toItem.getDto().getId() != workflowItem.getDto().getId()) {
			this.workflowItem = toItem;
			taskDTO.setPlace(workflowItem.getDto());
		}
	}
	
	public WorkflowItemGUI getWorkflowItem() {
		return workflowItem;
	}

	public ProjectGUI getProject() {
		return project;
	}

	public void setProject(ProjectGUI project) {
		this.project = project;
	}

	public void setWorkflowItem(WorkflowItemGUI workflowItem) {
		this.workflowItem = workflowItem;
	}

	public FocusPanel getHeader() {
		return header;
	}

	public TaskDTO getDto() {
		return taskDTO;
	}

	public void messageArrived(Message<TaskDTO> message) {
		TaskDTO payload = message.getPayload();
		if (payload.getId() == taskDTO.getId()) {
			this.taskDTO = payload;
			setupAccordingDTO(payload);
		}
	}
}
