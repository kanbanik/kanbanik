package com.googlecode.kanbanik.client.components.task;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.GetFirstTaskRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.GetFirstTaskResponseMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;
import static com.googlecode.kanbanik.client.api.Dtos.ClassOfServiceDto;


public class TaskAddingComponent extends AbstractTaskEditingComponent {

	private final Dtos.WorkflowitemDto inputQueue;
	
	private final Dtos.ProjectDto project;
	
	private static final GetFirstTaskResponseMessageListener getFirstTaskResponseMessageListener = new GetFirstTaskResponseMessageListener();

	public TaskAddingComponent(Dtos.ProjectDto project, Dtos.WorkflowitemDto inputQueue, HasClickHandlers clickHandler, Dtos.BoardDto boardDto) {
		super(clickHandler, boardDto);
		this.project = project;
		this.inputQueue = inputQueue;
	}

	@Override
	protected String getClassOfServiceAsString() {
		List<ClassOfServiceDto> classesOfService = ClassOfServicesManager.getInstance().getAll();
		if (classesOfService.size() != 0) {
            for (ClassOfServiceDto classOfService : classesOfService) {
                if (classOfService.getName() != null && classOfService.getName().equals("Standard")) {
                    return classOfService.getName();
                }
            }

			return classesOfService.iterator().next().getName();
		}
		
		return ClassOfServicesManager.getInstance().getDefaultClassOfService().getName();
	}

	@Override
	protected TaskDto createBasicDTO() {
		TaskDto taskDto = DtoFactory.taskDto();
		taskDto.setProjectId(project.getId());
		taskDto.setWorkflowitemId(inputQueue.getId());
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
        MessageBus.unregisterListener(GetFirstTaskResponseMessage.class, getFirstTaskResponseMessageListener);
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
		return 1;
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
