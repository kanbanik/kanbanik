package com.googlecode.kanbanik.client.components.task;

import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskChangedMessage;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;


public class TaskEditingComponent extends AbstractTaskEditingComponent {

	private TaskGui taskGui;

	public TaskEditingComponent(TaskGui taskGui, HasClickHandlers clickHandler) {
		super(clickHandler, taskGui.getDto().getWorkflowitem().getParentWorkflow().getBoard());
		this.taskGui = taskGui;
		initialize();
	}

	@Override
	protected void onClicked() {
		
		// retrieve the real task
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager
				.getInvoker()
				.<SimpleParams<TaskDto>, FailableResult<SimpleParams<TaskDto>>> invokeCommand(
						ServerCommand.GET_TASK,
						new SimpleParams<TaskDto>(taskGui.getDto()),
						new BaseAsyncCallback<FailableResult<SimpleParams<TaskDto>>>() {

							@Override
							public void success(FailableResult<SimpleParams<TaskDto>> result) {
								
								MessageBus.sendMessage(new TaskChangedMessage(result.getPayload().getPayload(), TaskEditingComponent.this));
								
								DeleteKeyListener.INSTANCE.stop();
								MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
								MessageBus.sendMessage(ChangeTaskSelectionMessage.selectOne(result.getPayload().getPayload(), TaskEditingComponent.this));
								
								doSetupAndShow();
							}

						});
		}});		
	}
	
	@Override
	protected String getClassOfServiceAsString() {
		ClassOfServiceDto classOfService = taskGui.getDto().getClassOfService();
		if (classOfService == null) {
			List<ClassOfServiceDto> classesOfService = ClassOfServicesManager.getInstance().getAll();
			if (classesOfService.size() != 0) {
				return classesOfService.iterator().next().getName();
			}
			
			return ClassOfServicesManager.getInstance().getDefaultClassOfService().getName();
		}
		
		return classOfService.getName();
		
	}

	@Override
	protected String getTicketId() {
		return taskGui.getDto().getTicketId();
	}

	@Override
	protected String getTaskName() {
		return taskGui.getDto().getName();
	}

	@Override
	protected String getDescription() {
		return taskGui.getDto().getDescription();
	}

	@Override
	protected String getId() {
		return taskGui.getDto().getId();
	}
	
	@Override
	protected TaskDto createBasicDTO() {
		return taskGui.getDto();
	}

	@Override
	protected int getVersion() {
		return taskGui.getDto().getVersion();
	}

	@Override
	protected String getUser() {
		if (taskGui.getDto().getAssignee() == null) {
			return "";
		}
		
		return taskGui.getDto().getAssignee().getUserName();
	}

	@Override
	protected String getDueDate() {
		return taskGui.getDto().getDueDate();
	}

}
