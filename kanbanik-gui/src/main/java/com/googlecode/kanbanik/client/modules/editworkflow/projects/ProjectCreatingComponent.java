package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleShell;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ProjectCreatingComponent extends AbstractProjectEditingComponent {

	final ServerCommandInvokerAsync serverCommandInvoker = GWT.create(ServerCommandInvoker.class);
	
	public ProjectCreatingComponent(HasClickHandlers clickHandlers) {
		super(clickHandlers);
	}

	@Override
	protected String getProjectName() {
		return "";
	}

	@Override
	protected void onOkClicked(ProjectDTO project) {
		final ProjectDTO dto = new ProjectDTO();
		dto.setId(-1);
		dto.setName(project.getName());
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						
						serverCommandInvoker.invokeCommand(ServerCommand.NEW_PROJECT, new SimpleShell<ProjectDto>(new ProjectDto(dto.getName())), new KanbanikAsyncCallback<SimpleShell<ProjectDto>>(){

							@Override
							public void success(SimpleShell<ProjectDto> result) {
								System.out.println("result");
								// TODO send message
							}
							
						});
						
						
//						configureWorkflowService.createNewProject(dto, new KanbanikAsyncCallback<ProjectDTO>() {
//
//							@Override
//							public void success(ProjectDTO result) {
//								MessageBus.sendMessage(new ProjectAddedMessage(result, ProjectCreatingComponent.this));
//							}
//
//						});				
					}
				}
		);
	}
}