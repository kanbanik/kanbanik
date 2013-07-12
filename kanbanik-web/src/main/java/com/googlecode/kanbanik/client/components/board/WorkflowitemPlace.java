package com.googlecode.kanbanik.client.components.board;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.GetFirstTaskRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetFirstTaskResponseMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class WorkflowitemPlace extends Composite implements
		MessageListener<TaskDto>, ModulesLifecycleListener {

	@UiField
	Label stateName;

	@UiField
	Label wipLimit;

	@UiField(provided = true)
	Widget contentPanel;

	interface MyUiBinder extends UiBinder<Widget, WorkflowitemPlace> {
	}
	
	private final GetFirstTaskRequestMessageListener getFirstTaskRequestMessageListener = new GetFirstTaskRequestMessageListener();

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final WorkflowitemDto workflowitemDto;

	private final DragController dragController;

	private final String projectDtoId;
	
	public WorkflowitemPlace(WorkflowitemDto workflowitemDto,
			ProjectDto projectDto, Widget body, DragController dragController) {

		this.workflowitemDto = workflowitemDto;
		this.projectDtoId = projectDto.getId();
		contentPanel = body;
		this.dragController = dragController;
		initWidget(uiBinder.createAndBindUi(this));

		String name = workflowitemDto.getName();
		if ("".equals(name)) {
			// an anonymous state - it has only body
			stateName.setVisible(false);
			wipLimit.setVisible(false);
		}
		stateName.setText(workflowitemDto.getName());

		setupWipLimit();

		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);

		MessageBus.registerListener(TaskAddedMessage.class, this);
		MessageBus.registerListener(TaskDeletedMessage.class, this);

	}

	private void setupWipLimit() {
		int wipLimitValue = workflowitemDto.getWipLimit();
		if (wipLimitValue <= 0) {
			wipLimit.setText("( - )");
		} else {
			wipLimit.setText("(" + Integer.toString(wipLimitValue) + ")");
		}
	}

	public void messageArrived(Message<TaskDto> message) {

		if (!(contentPanel instanceof TaskContainer)) {
			return;
		}

		TaskDto taskDto = message.getPayload();

		if (!isThisPlace(taskDto)) {
			return;
		}

		if (message instanceof TaskDeletedMessage) {
			((TaskContainer) contentPanel).removeTask(taskDto);
		} else if (message instanceof TaskAddedMessage) {
			if (((TaskContainer) contentPanel).containsTask(taskDto)) {
				return;
			}
			TaskGui task = new TaskGui(taskDto);
			dragController.makeDraggable(task, task.getHeader());
			((TaskContainer) contentPanel).add(task);
		}

	}

	private boolean isThisPlace(TaskDto taskDto) {
		if (taskDto.getWorkflowitem() == null) {
			return false;
		}

		if (workflowitemDto.getId().equals(taskDto.getWorkflowitem().getId())) {
			if (taskDto.getProject() == null) {
				return false;
			}

			return projectDtoId.equals(taskDto.getProject().getId());
		}

		return false;
	}

	public void activated() {
		if (!MessageBus.listens(TaskAddedMessage.class, this)) {
			MessageBus.registerListener(TaskAddedMessage.class, this);
		}

		if (!MessageBus.listens(TaskDeletedMessage.class, this)) {
			MessageBus.registerListener(TaskDeletedMessage.class, this);
		}
		
		if (!MessageBus.listens(GetFirstTaskRequestMessage.class, getFirstTaskRequestMessageListener)) {
			MessageBus.registerListener(GetFirstTaskRequestMessage.class, getFirstTaskRequestMessageListener);
		}
	}

	public void deactivated() {
		MessageBus.unregisterListener(TaskAddedMessage.class, this);
		MessageBus.unregisterListener(TaskDeletedMessage.class, this);
		MessageBus.unregisterListener(GetFirstTaskRequestMessage.class, getFirstTaskRequestMessageListener);
		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
	}
	
	class GetFirstTaskRequestMessageListener implements MessageListener<WorkflowitemDto> {

		@Override
		public void messageArrived(Message<WorkflowitemDto> message) {
			if (!(contentPanel instanceof TaskContainer)) {
				return;
			}

            if (message.getPayload() == null || workflowitemDto == null || !message.getPayload().equals(workflowitemDto)) {
                return;
            }
			
			TaskContainer container = (TaskContainer) contentPanel;
			List<TaskDto> tasks = container.getTasks();
			if (tasks.size() == 0) {
				MessageBus.sendMessage(new GetFirstTaskResponseMessage(null, this));
			} else {
				MessageBus.sendMessage(new GetFirstTaskResponseMessage(tasks.get(0), this));
			}
		}
		
	}
}

