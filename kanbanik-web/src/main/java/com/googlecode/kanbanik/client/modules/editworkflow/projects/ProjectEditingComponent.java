package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.ProjectEditedMessage;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ProjectEditingComponent extends AbstractProjectEditingComponent {

	private ProjectDto projectDto;
	
	public ProjectEditingComponent(ProjectDto projectDto, HasClickHandlers clickHandlers) {
		super(clickHandlers);
		this.projectDto = projectDto;
	}

	@Override
	protected String getProjectName() {
		return projectDto.getName();
	}

	@Override
	protected void onOkClicked(ProjectDTO project) {
		
		final ProjectDto toStore = new ProjectDto();
		toStore.setId(projectDto.getId());
		toStore.setName(project.getName());
		
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ProjectDto>, SimpleParams<ProjectDto>> invokeCommand(
				ServerCommand.SAVE_PROJECT,
				new SimpleParams<ProjectDto>(toStore),
				new KanbanikAsyncCallback<SimpleParams<ProjectDto>>() {

					@Override
					public void success(SimpleParams<ProjectDto> result) {
						projectDto.setName(toStore.getName());
						MessageBus.sendMessage(new ProjectEditedMessage(result.getPayload(), ProjectEditingComponent.this));
					}
				});
		
	}
	
	
}
