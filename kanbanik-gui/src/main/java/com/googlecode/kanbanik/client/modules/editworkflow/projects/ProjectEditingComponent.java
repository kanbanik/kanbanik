package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.ProjectEditedMessage;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;

public class ProjectEditingComponent extends AbstractProjectEditingComponent {

	private ProjectDTO projectDto;
	
	public ProjectEditingComponent(ProjectDTO projectDto, HasClickHandlers clickHandlers) {
		super(clickHandlers);
		this.projectDto = projectDto;
	}

	@Override
	protected String getProjectName() {
		return projectDto.getName();
	}

	@Override
	protected void onOkClicked(ProjectDTO project) {
		
		final ProjectDTO dto = new ProjectDTO();
		dto.setId(projectDto.getId());
		dto.setName(project.getName());
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.editProject(dto, new KanbanikAsyncCallback<ReturnObjectDTO>() {
							@Override
							public void success(ReturnObjectDTO result) {
								if (!result.isOK()) {
									new ErrorDialog(result.getMessage()).center();
								} else {
									projectDto.setName(dto.getName());
									MessageBus.sendMessage(new ProjectEditedMessage(dto, ProjectEditingComponent.this));
								}
							}

						});				
					}
				}
		);
	}
	
	
}
