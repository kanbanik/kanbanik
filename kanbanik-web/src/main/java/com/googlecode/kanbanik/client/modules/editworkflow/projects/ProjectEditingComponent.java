package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.ProjectEditedMessage;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
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
	protected void onOkClicked(final ProjectDto project) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ProjectDto>, FailableResult<SimpleParams<ProjectDto>>> invokeCommand(
				ServerCommand.SAVE_PROJECT,
				new SimpleParams<ProjectDto>(project),
				new ResourceClosingAsyncCallback<FailableResult<SimpleParams<ProjectDto>>>(ProjectEditingComponent.this) {

					@Override
					public void success(FailableResult<SimpleParams<ProjectDto>> result) {
						projectDto = result.getPayload().getPayload();
						MessageBus.sendMessage(new ProjectEditedMessage(result.getPayload().getPayload(), ProjectEditingComponent.this));
					}
				});
		}});
		
	}

	@Override
	protected ProjectDto createProject() {
		ProjectDto project = new ProjectDto();
		project.setName(projectDto.getName());
		project.setId(projectDto.getId());
		project.setBoards(projectDto.getBoards());
		project.setTasks(projectDto.getTasks());
		project.setVersion(projectDto.getVersion());
		return project;
	}
	
	
}
