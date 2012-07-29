package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.AbstractDeletingComponent;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.ProjectDeletedMessage;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ProjectDeletingComponent extends AbstractDeletingComponent {

	private ProjectDto projectDto;
	
	public ProjectDeletingComponent(ProjectDto projectDto, HasClickHandlers hasClickHandler) {
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
		ServerCommandInvokerManager.getInvoker().<SimpleParams<ProjectDto>, FailableResult<VoidParams>> invokeCommand(
				ServerCommand.DELETE_PROJECT,
				new SimpleParams<ProjectDto>(projectDto),
				new KanbanikAsyncCallback<FailableResult<VoidParams>>() {

					@Override
					public void success(FailableResult<VoidParams> result) {
						if (!result.isSucceeded()) {
							new ErrorDialog(result.getMessage()).center();
						} else {
							MessageBus.sendMessage(new ProjectDeletedMessage(projectDto, this));
						}	
					}
				});
		}});
	}

}
