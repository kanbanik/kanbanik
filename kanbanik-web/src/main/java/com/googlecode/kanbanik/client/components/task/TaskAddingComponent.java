package com.googlecode.kanbanik.client.components.task;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.GetFirstTaskRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetFirstTaskResponseMessage;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;


public class TaskAddingComponent extends AbstractTaskEditingComponent {

	private final WorkflowitemDto inputQueue;
	
	private final ProjectDto project;

	private BoardDto board;
	
	private static final GetFirstTaskResponseMessageListener getFirstTaskResponseMessageListener = new GetFirstTaskResponseMessageListener();

	public TaskAddingComponent(ProjectDto project, WorkflowitemDto inputQueue, HasClickHandlers clickHandler) {
		super(clickHandler, inputQueue.getParentWorkflow().getBoard());
		board = inputQueue.getParentWorkflow().getBoard();
		this.project = project;
		this.inputQueue = inputQueue;
		initialize();
	}

	@Override
	protected String getClassOfServiceAsString() {
		List<ClassOfServiceDto> classesOfService = ClassOfServicesManager.getInstance().getForBoard(board);
		if (classesOfService.size() != 0) {
			return classesOfService.iterator().next().getName();
		}
		
		return ClassOfServicesManager.getInstance().getDefaultClassOfService().getName();
	}

	@Override
	protected TaskDto createBasicDTO() {
		TaskDto taskDto = new TaskDto();
		taskDto.setProject(project);
		taskDto.setWorkflowitem(inputQueue);
		taskDto.setOrder(findOrder());
		return taskDto;
	}

	private String findOrder() {
		if (!MessageBus.listens(GetFirstTaskResponseMessage.class, getFirstTaskResponseMessageListener)) {
			MessageBus.registerListener(GetFirstTaskResponseMessage.class, getFirstTaskResponseMessageListener);
		}
		
		
		MessageBus.sendMessage(new GetFirstTaskRequestMessage(inputQueue, this));
		
		// this is safe - the messaging is synchronous even it does not look that way
		TaskDto firstTaskOrder = getFirstTaskResponseMessageListener.getFirstTask();
		return firstTaskOrder != null ? getNewTaskOrder(firstTaskOrder) : "0";
	}

	private String getNewTaskOrder(TaskDto firstTaskOrder) {
		return new BigDecimal(firstTaskOrder.getOrder()).subtract(new BigDecimal(100)).toString();
	}
	
	@Override
	protected String getTicketId() {
		return "";
	}

	@Override
	protected String getTaskName() {
		return "";
	}

	@Override
	protected String getDescription() {
		return "";
	}

	@Override
	protected String getId() {
		return null;
	}

	@Override
	protected int getVersion() {
		return 0;
	}

	@Override
	protected String getUser() {
		return "";
	}

	@Override
	protected String getDueDate() {
		return null;
	}
	
}

class GetFirstTaskResponseMessageListener implements MessageListener<TaskDto> {
	
	private TaskDto firstTask;
	
	@Override
	public void messageArrived(Message<TaskDto> message) {
		firstTask = message.getPayload();
	}
	
	public TaskDto getFirstTask() {
		return firstTask;
	}
	
}
