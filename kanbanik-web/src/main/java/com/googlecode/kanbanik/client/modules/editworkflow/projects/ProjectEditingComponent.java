package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.Dtos.ProjectDto;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.CommandNames;

public class ProjectEditingComponent extends AbstractProjectEditingComponent implements ModulesLifecycleListener, MessageListener<ProjectDto> {

	private ProjectDto projectDto;
	
	public ProjectEditingComponent(ProjectDto projectDto, HasClickHandlers clickHandlers) {
		super(clickHandlers, "Edit Project");
		this.projectDto = projectDto;
		
		MessageBus.registerListener(ProjectChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	@Override
	protected String getProjectName() {
		return projectDto.getName();
	}

	@Override
	protected void onOkClicked(final ProjectDto project) {
        project.setCommandName(CommandNames.EDIT_PROJECT.name);

        ServerCaller.<Dtos.ProjectDto, Dtos.ProjectDto>sendRequest(
                project,
                Dtos.ProjectDto.class,
                new ResourceClosingCallback<Dtos.ProjectDto>(ProjectEditingComponent.this) {

                    @Override
                    public void success(Dtos.ProjectDto response) {
                        projectDto = response;
                        MessageBus.sendMessage(new ProjectEditedMessage(projectDto, ProjectEditingComponent.this));
                        MessageBus.sendMessage(new ProjectChangedMessage(projectDto, ProjectEditingComponent.this));
                    }
                }
        );

	}

	@Override
	protected ProjectDto createProject() {
		ProjectDto project = DtoFactory.projectDto();
		project.setName(projectDto.getName());
		project.setId(projectDto.getId());
		project.setBoardIds(projectDto.getBoardIds());
		project.setVersion(projectDto.getVersion());
		return project;
	}

	@Override
	public void messageArrived(Message<ProjectDto> message) {
		if (message.getPayload() == null) {
			return;
		}
		
		if (message.getPayload().getId().equals(projectDto.getId())) {
			projectDto = message.getPayload();
		}
	}

	@Override
	public void activated() {
		if (!MessageBus.listens(ProjectChangedMessage.class, this)) {
			MessageBus.registerListener(ProjectChangedMessage.class, this);	
		}		
	}

	@Override
	public void deactivated() {
		MessageBus.unregisterListener(ProjectChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
	
	
}
