package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectAddedMessage;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;
import com.googlecode.kanbanik.dto.CommandNames;

public class ProjectCreatingComponent extends AbstractProjectEditingComponent {

	final ServerCommandInvokerAsync serverCommandInvoker = GWT.create(ServerCommandInvoker.class);
	
	public ProjectCreatingComponent(HasClickHandlers clickHandlers) {
		super(clickHandlers, "Add Project");
	}

	@Override
	protected String getProjectName() {
		return "";
	}

	@Override
	protected void onOkClicked(final Dtos.ProjectDto project) {

        project.setCommandName(CommandNames.CREATE_PROJECT.name);

        ServerCaller.<Dtos.ProjectDto, Dtos.ProjectDto>sendRequest(
                project,
                Dtos.ProjectDto.class,
                new ResourceClosingCallback<Dtos.ProjectDto>(ProjectCreatingComponent.this) {

                    @Override
                    public void success(Dtos.ProjectDto response) {
                        MessageBus.sendMessage(new ProjectAddedMessage(response, ProjectCreatingComponent.this));;
                    }
                }
        );
	}

	@Override
	protected Dtos.ProjectDto createProject() {
		Dtos.ProjectDto project = DtoFactory.projectDto();
		project.setId(null);
        project.setVersion(1);
		return project;
	}
}