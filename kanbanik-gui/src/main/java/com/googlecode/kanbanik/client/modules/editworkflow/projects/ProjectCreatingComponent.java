package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.shared.ProjectDTO;

public class ProjectCreatingComponent extends AbstractProjectEditingComponent {

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
						configureWorkflowService.createNewProject(dto, new KanbanikAsyncCallback<ProjectDTO>() {

							@Override
							public void success(ProjectDTO result) {
								MessageBus.sendMessage(new ProjectAddedMessage(result, ProjectCreatingComponent.this));
							}

						});				
					}
				}
		);
	}
}