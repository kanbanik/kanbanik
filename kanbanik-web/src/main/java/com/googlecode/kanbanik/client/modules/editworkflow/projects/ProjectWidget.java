package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.api.Dtos.ProjectDto;

public class ProjectWidget extends Composite implements HasDragHandle, MessageListener<ProjectDto>, ModulesLifecycleListener {
	
	private ProjectDto dto;
	
	private String position;

	@UiField
	PushButton editButton;
	
	@UiField
	PushButton deleteButton;
	
	@UiField
	Label projectName;

	@UiField
	FocusPanel header;
	
	interface MyUiBinder extends UiBinder<Widget, ProjectWidget> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public ProjectWidget(String position, ProjectDto project) {
		this.dto = project;
		this.position = position;
		
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		MessageBus.registerListener(ProjectEditedMessage.class, this);
		MessageBus.registerListener(ProjectChangedMessage.class, this);
		
		projectName.setText(project.getName());
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		new ProjectDeletingComponent(project, deleteButton);
		new ProjectEditingComponent(project, editButton);
	}

	public ProjectDto getDto() {
		return dto;
	}
	
	public boolean isNewPosition(String newPosition) {
		return !position.equals(newPosition);
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public FocusPanel getHeader() {
		return header;
	}

	public Widget getDragHandle() {
		return header;
	}

	public void messageArrived(Message<ProjectDto> message) {
		if (message.getPayload() == null) {
			return;
		}
		
		String payloadId = message.getPayload().getId();
		if (payloadId == null || !payloadId.equals(dto.getId())) {
			return;
		}
		
		dto = message.getPayload();
		projectName.setText(message.getPayload().getName());
	}

	public void activated() {
		if (!MessageBus.listens(ProjectEditedMessage.class, this)) {
			MessageBus.registerListener(ProjectEditedMessage.class, this);	
		}
		
		if (!MessageBus.listens(ProjectChangedMessage.class, this)) {
			MessageBus.registerListener(ProjectChangedMessage.class, this);	
		}
	}

	public void deactivated() {
		MessageBus.unregisterListener(ProjectEditedMessage.class, this);
		MessageBus.unregisterListener(ProjectChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
}
