package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.ProjectEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.CommandNames;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;
import com.googlecode.kanbanik.client.api.Dtos.ProjectDto;

public class ProjectsToBoardAdding extends Composite implements ModulesLifecycleListener {

    private static final String ON_BOARD = "RIGHT";

    private static final String TO_BE_ADDED = "LEFT";

    private BoardWithProjectsDto boardWithProjects;

    private List<ProjectDto> projects;

    @UiField
    FlowPanel toBeAdded;

    @UiField
    FlowPanel projectsOfBoard;

    // just because PickupDragController accepts only absolute panels
    @UiField
    AbsolutePanel panelWithDraggablePanels;

    private PickupDragController dragController;

    private ProjectChangedListener projectChangedListener = new ProjectChangedListener();

    private ProjectMovedDropController toBeAddedDropController;

    private ProjectMovedDropController projectsOfBoardDropController;

    interface MyUiBinder extends UiBinder<Widget, ProjectsToBoardAdding> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public ProjectsToBoardAdding(BoardWithProjectsDto boardWithProjects, List<ProjectDto> allProjects) {
        super();
        this.boardWithProjects = boardWithProjects;
        this.projects = allProjects;

        new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);

        initWidget(uiBinder.createAndBindUi(this));

        dragController = new PickupDragController(panelWithDraggablePanels, false);
        init();

        MessageBus.registerListener(ProjectAddedMessage.class, projectChangedListener);
        MessageBus.registerListener(ProjectDeletedMessage.class, projectChangedListener);
    }

    public void disable() {
        boardWithProjects = null;
        removeAllProjectsFromPanel(projectsOfBoard);
        String message = "It is not possible to move the project if there is no board";
        toBeAddedDropController.disableDrop(message);
        projectsOfBoardDropController.disableDrop(message);
    }

    private void init() {
        projectsOfBoard.add(new Label(""));
        toBeAdded.add(new Label(""));
        toBeAddedDropController = new ProjectMovedDropController(toBeAdded, TO_BE_ADDED, new LeftListener());
        dragController.registerDropController(toBeAddedDropController);
        projectsOfBoardDropController = new ProjectMovedDropController(projectsOfBoard, ON_BOARD, new RightListener());
        dragController.registerDropController(projectsOfBoardDropController);


        if (boardWithProjects != null) {
            for (ProjectDto project : projects) {
                if (contains(asNewProject(boardWithProjects.getProjectsOnBoard()), project)) {
                    addProjectToPanel(projectsOfBoard, ON_BOARD, project);
                } else {
                    addProjectToPanel(toBeAdded, TO_BE_ADDED, project);
                }
            }
        } else {
            disable();
        }
    }

    private List<ProjectDto> asNewProject(List<com.googlecode.kanbanik.dto.ProjectDto> oldProjects) {
        List<ProjectDto> projects = new ArrayList<ProjectDto>();
        for (com.googlecode.kanbanik.dto.ProjectDto oldProject : oldProjects) {
            ProjectDto project = DtoFactory.projectDto();
            project.setId(oldProject.getId());
            project.setName(oldProject.getName());
            project.setVersion(oldProject.getVersion());
            List<String> boardIds = new ArrayList<String>();
            for (BoardDto boardDto : oldProject.getBoards()) {
                boardIds.add(boardDto.getId());
            }
            project.setBoardIds(boardIds);

            projects.add(project);
        }

        return projects;
    }

    private void addProjectToPanel(Panel panel, String position, ProjectDto project) {
        ProjectWidget projectWidget = new ProjectWidget(position, project);
        panel.add(projectWidget);
        dragController.makeDraggable(projectWidget);
    }

    private void removeAllProjectsFromPanel(FlowPanel panel) {
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            if (panel.getWidget(i) instanceof ProjectWidget) {
                panel.remove(i);
            }
        }
    }

    private boolean contains(List<ProjectDto> projectsOnBoard, ProjectDto project) {
        for (ProjectDto onBoard : projectsOnBoard) {
            if (onBoard.getId().equals(project.getId())) {
                return true;
            }
        }

        return false;
    }

    private void executeCommand(final CommandNames commandName, Widget widgets) {
        final ProjectDto projectDto = extractDTOs(widgets);
        Dtos.ProjectWithBoardDto toStore = new DtoFactory().projectWithBoardDto();

        toStore.setCommandName(commandName.name);
        toStore.setProject(projectDto);
        toStore.setBoardId(boardWithProjects.getBoard().getId());

        ServerCaller.<Dtos.ProjectWithBoardDto, Dtos.ProjectWithBoardDto>sendRequest(
                toStore,
                Dtos.ProjectWithBoardDto.class,
                new ServerCallCallback<Dtos.ProjectWithBoardDto>() {

                    @Override
                    public void success(Dtos.ProjectWithBoardDto response) {
                        super.success(response);
                        MessageBus.sendMessage(new ProjectChangedMessage(response.getProject(), ProjectsToBoardAdding.this));
                    }

                    @Override
                    public void anyFailure() {
                        super.anyFailure();
                        rollbackAfterFail(projectDto, commandName);
                    }
                }


        );
    }

    private void rollbackAfterFail(ProjectDto dto, CommandNames commandName) {
        removedProjectFromPanel(dto);
        if (CommandNames.REMOVE_PROJECT_FROM_BOARD.equals(commandName)) {
            addProjectToPanel(projectsOfBoard, ON_BOARD, dto);
        } else if (CommandNames.ADD_PROJECT_TO_BOARD.equals(commandName)) {
            addProjectToPanel(toBeAdded, TO_BE_ADDED, dto);
        }
    }

    private ProjectDto extractDTOs(Widget widget) {
        if (widget instanceof ProjectWidget) {
            return ((ProjectWidget) widget).getDto();
        }

        return null;
    }

    class LeftListener implements WidgetsDropListener {
        public void dropped(List<Widget> widgets) {
            // anyway only one...
            for (Widget widget : widgets) {
                executeCommand(CommandNames.REMOVE_PROJECT_FROM_BOARD, widget);
            }

        }
    }

    class RightListener implements WidgetsDropListener {
        public void dropped(List<Widget> widgets) {
            // anyway only one...
            for (Widget widget : widgets) {
                executeCommand(CommandNames.ADD_PROJECT_TO_BOARD, widget);
            }
        }
    }

    class ProjectChangedListener implements MessageListener<ProjectDto> {

        public void messageArrived(Message<ProjectDto> message) {
            if (message instanceof ProjectAddedMessage) {
                addProjectToPanel(toBeAdded, TO_BE_ADDED, message.getPayload());
            } else if (message instanceof ProjectDeletedMessage) {
                removedProjectFromPanel(message.getPayload());
            }
        }

    }

    private void removedProjectFromPanel(ProjectDto payload) {
        if (!removeIfPresent(toBeAdded, payload)) {
            removeIfPresent(projectsOfBoard, payload);
        }
    }

    private boolean removeIfPresent(FlowPanel panel, ProjectDto payload) {
        int toDelete = -1;
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            if (panel.getWidget(i) instanceof ProjectWidget) {
                ProjectWidget widget = (ProjectWidget) panel.getWidget(i);
                if (widget.getDto().getId() == payload.getId()) {
                    toDelete = i;
                    break;
                }
            }
        }
        if (toDelete != -1) {
            return panel.remove(toDelete);
        }

        return false;
    }

    public void activated() {
        if (!MessageBus.listens(ProjectAddedMessage.class, projectChangedListener)) {
            MessageBus.registerListener(ProjectAddedMessage.class, projectChangedListener);
        }

        if (!MessageBus.listens(ProjectDeletedMessage.class, projectChangedListener)) {
            MessageBus.registerListener(ProjectDeletedMessage.class, projectChangedListener);
        }
    }

    public void deactivated() {
        MessageBus.unregisterListener(ProjectAddedMessage.class, projectChangedListener);
        MessageBus.unregisterListener(ProjectDeletedMessage.class, projectChangedListener);
        new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
    }
}