package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.AbstractDeletingComponent;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.ProjectDeletedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.ProjectEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ProjectDeletingComponent extends AbstractDeletingComponent implements ModulesLifecycleListener, MessageListener<ProjectDto> {

	private ProjectDto projectDto;
	
	public ProjectDeletingComponent(ProjectDto projectDto, HasClickHandlers hasClickHandler) {
		super(hasClickHandler);
		this.projectDto = projectDto;
		
		MessageBus.registerListener(ProjectEditedMessage.class, this);
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
				new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(ProjectDeletingComponent.this) {

					@Override
					public void success(FailableResult<VoidParams> result) {
						MessageBus.sendMessage(new ProjectDeletedMessage(projectDto, this));
					}
				});
		}});
	}
	
	public void activated() {
		if (!MessageBus.listens(ProjectEditedMessage.class, this)) {
			MessageBus.registerListener(ProjectEditedMessage.class, this);	
		}
	}

	public void deactivated() {
		MessageBus.unregisterListener(ProjectEditedMessage.class, this);
	}

	public void messageArrived(Message<ProjectDto> message) {
		if (message.getPayload().equals(projectDto)) {
			projectDto = message.getPayload();
		}
	}

}
