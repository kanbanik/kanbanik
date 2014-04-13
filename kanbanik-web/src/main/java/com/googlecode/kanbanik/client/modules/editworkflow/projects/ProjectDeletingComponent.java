package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.Dtos.ProjectDto;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectEditedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.AbstractDeletingComponent;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class ProjectDeletingComponent extends AbstractDeletingComponent implements ModulesLifecycleListener, MessageListener<ProjectDto> {

	private ProjectDto projectDto;
	
	public ProjectDeletingComponent(ProjectDto projectDto, HasClickHandlers hasClickHandler) {
		super(hasClickHandler);
		this.projectDto = projectDto;
		
		MessageBus.registerListener(ProjectEditedMessage.class, this);
		MessageBus.registerListener(ProjectChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	@Override
	protected String getMessageSpecificPart() {
		return "Project with name: '" + projectDto.getName() + "'";
	}

	@Override
	protected void onOkClicked() {

        projectDto.setCommandName(CommandNames.DELETE_PROJECT.name);
        projectDto.setSessionId(CurrentUser.getInstance().getSessionId());

        ServerCaller.<Dtos.ProjectDto, Dtos.EmptyDto>sendRequest(
                projectDto,
                Dtos.EmptyDto.class,
                new ResourceClosingCallback<Dtos.EmptyDto>(ProjectDeletingComponent.this) {

                    @Override
                    public void success(Dtos.EmptyDto response) {
                        MessageBus.sendMessage(new ProjectDeletedMessage(projectDto, this));
                    }
                }
        );
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

	public void messageArrived(Message<ProjectDto> message) {
		if (message.getPayload() == null) {
			return;
		}
		
		if (message.getPayload().getId().equals(projectDto.getId())) {
			projectDto = message.getPayload();
		}
	}

}
