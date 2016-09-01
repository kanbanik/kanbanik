package com.googlecode.kanbanik.client.components.board;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.*;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class WorkflowitemPlace extends Composite implements
		MessageListener<TaskDto>, ModulesLifecycleListener {

	@UiField
	Label nameWithWipLimitField;

	@UiField(provided = true)
	Widget contentPanel;

    @UiField
    TableRowElement nameWithWipLimit;

    private BoardsFilter filter;

    protected interface Style extends CssResource {
        String visibleHeader();

        String hiddenHeader();

        String stateStyle();

        String stackStyle();
    }

    @UiField
    protected Style style;

	interface MyUiBinder extends UiBinder<Widget, WorkflowitemPlace> {
	}
	
	private final GetFirstTaskRequestMessageListener getFirstTaskRequestMessageListener = new GetFirstTaskRequestMessageListener();

    private final GetTaskByIdRequestMessageListener getTaskByIdRequestMessageListener = new GetTaskByIdRequestMessageListener();

    private TaskFilterChangeListener taskFilterChangeListener = new TaskFilterChangeListener();

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final Dtos.WorkflowitemDto workflowitemDto;

	private final DragController dragController;

	private final String projectDtoId;

    private Dtos.BoardDto board;

	public WorkflowitemPlace(Dtos.WorkflowitemDto workflowitemDto,
                             Dtos.ProjectDto projectDto, Widget body, DragController dragController, Dtos.BoardDto board) {
        this.board = board;
		this.workflowitemDto = workflowitemDto;
		this.projectDtoId = projectDto.getId();
		contentPanel = body;
		this.dragController = dragController;
		initWidget(uiBinder.createAndBindUi(this));

		String name = workflowitemDto.getName();
		if ("".equals(name)) {
			// an anonymous state - it has only body
			nameWithWipLimitField.setVisible(false);
            nameWithWipLimit.addClassName(style.hiddenHeader());
		} else {
            nameWithWipLimit.addClassName(style.visibleHeader());
        }

		setupNameWithWipLimit();

		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);

		MessageBus.registerListener(TaskAddedMessage.class, this);
		MessageBus.registerListener(TaskDeletedMessage.class, this);
        MessageBus.registerListener(GetFirstTaskRequestMessage.class, getFirstTaskRequestMessageListener);
        MessageBus.registerListener(GetTaskByIdRequestMessage.class, getTaskByIdRequestMessageListener);
        MessageBus.registerListener(FilterChangedMessage.class, taskFilterChangeListener);
	}

	private void setupNameWithWipLimit() {
        String nameWithWipLimitText = workflowitemDto.getName();

		int wipLimitValue = workflowitemDto.getWipLimit();
		if (wipLimitValue > 0) {
            nameWithWipLimitField.setStyleName(style.stateStyle());
            nameWithWipLimitText += " [" + Integer.toString(wipLimitValue) + "]";
		} else {
            nameWithWipLimitField.setStyleName(style.stackStyle());
        }

        nameWithWipLimitField.setText(nameWithWipLimitText);
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
			((TaskContainer) contentPanel).removeTask(taskDto, ((TaskDeletedMessage) message).isPartOfMove());
		} else if (message instanceof TaskAddedMessage) {

			if (((TaskContainer) contentPanel).containsTask(taskDto)) {
				return;
			}

            HasVisibility addedTask = ((TaskContainer) contentPanel).add(taskDto, filter, dragController);

            if (((TaskAddedMessage) message).isPartOfMove()) {
                addedTask.setVisible(((TaskAddedMessage) message).wasVisible());
            }
		}

	}

	private boolean isThisPlace(TaskDto taskDto) {
		if (taskDto.getWorkflowitemId() == null) {
			return false;
		}

		if (workflowitemDto.getId().equals(taskDto.getWorkflowitemId())) {
			if (taskDto.getProjectId() == null) {
				return false;
			}

			return projectDtoId.equals(taskDto.getProjectId());
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

		if (!MessageBus.listens(GetTaskByIdRequestMessage.class, getTaskByIdRequestMessageListener)) {
			MessageBus.registerListener(GetTaskByIdRequestMessage.class, getTaskByIdRequestMessageListener);
		}

        if (!MessageBus.listens(FilterChangedMessage.class, taskFilterChangeListener)) {
			MessageBus.registerListener(FilterChangedMessage.class, taskFilterChangeListener);
		}
	}

	public void deactivated() {
		MessageBus.unregisterListener(TaskAddedMessage.class, this);
		MessageBus.unregisterListener(TaskDeletedMessage.class, this);
		MessageBus.unregisterListener(GetFirstTaskRequestMessage.class, getFirstTaskRequestMessageListener);
        MessageBus.unregisterListener(GetTaskByIdRequestMessage.class, getTaskByIdRequestMessageListener);
        MessageBus.unregisterListener(FilterChangedMessage.class, taskFilterChangeListener);
	}
	
	class GetFirstTaskRequestMessageListener implements MessageListener<Dtos.WorkflowitemDto> {

		@Override
		public void messageArrived(Message<Dtos.WorkflowitemDto> message) {
			if (!(contentPanel instanceof TaskContainer)) {
				return;
			}

            if (message.getPayload() == null || workflowitemDto == null || !message.getPayload().equals(workflowitemDto)) {
                return;
            }
			
			TaskContainer container = (TaskContainer) contentPanel;
			List<TaskDto> tasks = container.getTasks();
			if (tasks.isEmpty()) {
				MessageBus.sendMessage(new GetFirstTaskResponseMessage(null, this));
			} else {
				MessageBus.sendMessage(new GetFirstTaskResponseMessage(tasks.get(0), this));
			}
		}
		
	}

    class GetTaskByIdRequestMessageListener implements MessageListener<String> {

        @Override
        public void messageArrived(Message<String> message) {
            if (!(contentPanel instanceof TaskContainer)) {
                return;
            }

            TicketTaskContainer container = (TicketTaskContainer) contentPanel;
            TaskGui task = container.getTaskGuiById(message.getPayload());
            if (task != null) {
                MessageBus.sendMessage(new GetTaskByIdResponseMessage(task.getDto(), task.isVisible(), this));
            }
        }
    }

    class TaskFilterChangeListener implements MessageListener<BoardsFilter> {

        @Override
        public void messageArrived(Message<BoardsFilter> message) {
            if (message.getPayload() == null) {
                return;
            }

            filter = message.getPayload();
        }
    }
}

