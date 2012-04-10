package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.AbstractDeletingComponent;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.ProjectDeletedMessage;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;

public class ProjectDeletingComponent extends AbstractDeletingComponent {

	private final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);
	
	private ProjectDTO projectDto;
	
	public ProjectDeletingComponent(ProjectDTO projectDto, HasClickHandlers hasClickHandler) {
		super(hasClickHandler);
		this.projectDto = projectDto;
	}

	@Override
	protected String getMessageSpecificPart() {
		return "Project with name: '" + projectDto.getName() + "'";
	}

	@Override
	protected void onOkClicked() {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.deleteProject(projectDto, new KanbanikAsyncCallback<ReturnObjectDTO>() {

							@Override
							public void success(ReturnObjectDTO result) {
								if (!result.isOK()) {
									new ErrorDialog(result.getMessage()).center();
								} else {
									MessageBus.sendMessage(new ProjectDeletedMessage(projectDto, this));
								}	
							}
						});
					}
				}
				);	
	}

}
