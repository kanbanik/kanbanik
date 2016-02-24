package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.task.TaskAddingComponent;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.project.GetAllProjectsRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.GetAllProjectsResponseMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.security.CurrentUser;

import java.util.ArrayList;
import java.util.List;

public class ProjectHeader extends Composite implements ModulesLifecycleListener, MessageListener<Dtos.ProjectDto> {

    interface MyUiBinder extends UiBinder<Widget, ProjectHeader> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label projectName;
	
	@UiField
	PushButton addButton;

    private Dtos.BoardDto board;

    private Dtos.ProjectDto project;

	public ProjectHeader(final Dtos.BoardDto board, final Dtos.ProjectDto project) {
        this.board = board;
        this.project = project;
        initWidget(uiBinder.createAndBindUi(this));
		projectName.setText(project.getName());
		Dtos.WorkflowitemDto rootDto = !board.getWorkflow().getWorkflowitems().isEmpty() ? board.getWorkflow().getWorkflowitems().get(0) : null;
		
		if (rootDto != null) {
			addButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));	
		} else {
			// the board has no workflow, disable add button
			disableAddButton("It is not possible to add a task to a board when the board has no workflow.");
		}

        if (addButton.isEnabled()) {
            if (!CurrentUser.getInstance().canAddTaskTo(board, project)) {
                disableAddButton("This user '" + CurrentUser.getInstance().getUser().getUserName() + "' does not have permissions to create a task on this board and project");
            }
        }
		
		new TaskAddingComponent(project, getInputQueue(rootDto), addButton, board);

        MessageBus.registerListener(GetAllProjectsRequestMessage.class, this);

        new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
	}

    private void disableAddButton(String msg) {
        addButton.setEnabled(false);
        addButton.setTitle(msg);
        addButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addDisabledButtonImage()));
    }
	
	private Dtos.WorkflowitemDto getInputQueue(Dtos.WorkflowitemDto root) {
		if (root == null) {
			return null;
		}
		
		
		if (root.getNestedWorkflow().getWorkflowitems().isEmpty()) {
			return root;
		} else {
			return getInputQueue(root.getNestedWorkflow().getWorkflowitems().get(0));
		}
	}

    @Override
    public void activated() {
        if (!MessageBus.listens(GetAllProjectsRequestMessage.class, this)) {
            MessageBus.registerListener(GetAllProjectsRequestMessage.class, this);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(GetAllProjectsRequestMessage.class, this);
    }

    @Override
    public void messageArrived(Message<Dtos.ProjectDto> message) {
        Dtos.BoardWithProjectsDto boardWithProjectsDto = DtoFactory.boardWithProjectsDto();
        boardWithProjectsDto.setBoard(board);
        List<Dtos.ProjectDto> projects = new ArrayList<Dtos.ProjectDto>();
        projects.add(project);
        boardWithProjectsDto.setProjectsOnBoard(DtoFactory.projectsDto(projects));

        MessageBus.sendMessage(new GetAllProjectsResponseMessage(boardWithProjectsDto, this));
    }

    public void init() {
        if (getParent() != null) {
            // hack for firefox
            getParent().getElement().getStyle().setBackgroundColor("#e6e9ec");
        }
    }
	
}
