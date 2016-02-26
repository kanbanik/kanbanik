package com.googlecode.kanbanik.client.modules;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikProgressBar;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.board.BoardPanel;
import com.googlecode.kanbanik.client.components.board.BoardsPanel;
import com.googlecode.kanbanik.client.components.board.NoContentWarningPanel;
import com.googlecode.kanbanik.client.components.board.ProjectHeader;
import com.googlecode.kanbanik.client.components.task.GlobalKeyListener;
import com.googlecode.kanbanik.client.components.task.DeleteTasksMessageListener;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.modules.KanbanikModule.ModuleInitializeCallback;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardGuiBuilder;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.ExtendedWorkflowitem;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WipLimitGuard;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoardsModule {

	private static final BoardStyle style = KanbanikResources.INSTANCE
			.boardStyle();

	static {
		style.ensureInjected();
		GlobalKeyListener.INSTANCE.initialize();
		new DeleteTasksMessageListener().initialize();
	}

    /**
     * Adds all the tasks which are taken as an argument in a batches of 10. This avoids freezing the browser if there are lots of tasks
     */
    class TaskAddingCommands implements Scheduler.RepeatingCommand {

        private Iterator<Dtos.TaskDto> linearizedTasks;

        public TaskAddingCommands(List<Dtos.TaskDto> linearizedTasks) {
            this.linearizedTasks = linearizedTasks.iterator();
        }

        @Override
        public boolean execute() {
            int i = 0;
            while (linearizedTasks.hasNext()) {
                i ++;
                MessageBus.sendMessage(new TaskAddedMessage(linearizedTasks.next(), this));
                if (i >= 10) {
                    // stop and continue later
                    return true;
                }
            }

            KanbanikProgressBar.hide();
            // stop execution
            return false;
        }
    }

	private void addTasks(final Dtos.BoardsWithProjectsDto result) {
        List<Dtos.TaskDto> linearizedTasks = new ArrayList<>();
		for (Dtos.BoardWithProjectsDto boardWithProjects : result.getValues()) {
			for (Dtos.TaskDto task : boardWithProjects.getBoard().getTasks()) {
				linearizedTasks.add(task);
			}
		}

        if (linearizedTasks.size() > 0) {
            Scheduler.get().scheduleIncremental(new TaskAddingCommands(linearizedTasks));
        } else {
            KanbanikProgressBar.hide();
        }
	}

	private Widget buildBoard(Dtos.BoardsWithProjectsDto result) {
		AbsolutePanel boardsPanelWrapper = new AbsolutePanel();
	    BoardsPanel panel = new BoardsPanel();
		boardsPanelWrapper.add(panel);
		panel.getElement().setId("boards");

		List<Dtos.BoardWithProjectsDto> boardsWithProjects = result.getValues();

		if (boardsWithProjects == null || boardsWithProjects.size() == 0) {
			createNoBoardsPanel(panel);
		} else {
			createBoardsPanel(panel, boardsPanelWrapper, boardsWithProjects);
		}

		return boardsPanelWrapper;
	}

	private void createBoardsPanel(BoardsPanel panel,
								   AbsolutePanel boardsPanelWrapper, List<Dtos.BoardWithProjectsDto> boardsWithProjects) {
		BoardGuiBuilder boardBuilder = new BoardGuiBuilder();
		boardsPanelWrapper.addDomHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
			}
		}, ClickEvent.getType());

		PickupDragController dragController = new PickupDragController(
				boardsPanelWrapper, false);

		dragController.setBehaviorMultipleSelection(true);
		dragController.setBehaviorDragStartSensitivity(3);
		dragController.setBehaviorCancelDocumentSelections(true);

		for (Dtos.BoardWithProjectsDto boardWithProjects : boardsWithProjects) {
			Dtos.BoardDto board = boardWithProjects.getBoard();

			FlexTable boardTable = new FlexTable();
			boardTable.getElement().getStyle().setProperty("borderSpacing", "0px");
			AbsolutePanel projects = new AbsolutePanel();

			projects.add(boardTable);
			List<Dtos.ProjectDto> projectsOnBoard = boardWithProjects.getProjectsOnBoard() != null ? boardWithProjects.getProjectsOnBoard().getValues() : null;
			if (projectsOnBoard == null || projectsOnBoard.size() == 0) {
				addNoProjects(boardTable);
			} else {
				addProjcets(dragController, board, boardBuilder, boardTable, projectsOnBoard);
			}

			panel.addBoard(new BoardPanel(projects, board, projectsOnBoard));

		}
	}

	private void addNoProjects(FlexTable boardTable) {
		boardTable.setWidth("100%");
		boardTable
				.setWidget(
						0,
						0,
						new NoContentWarningPanel(
								"There are no projects on this board. Please add at least one project."));
	}

	private void addProjcets(PickupDragController dragController,
			Dtos.BoardDto board, BoardGuiBuilder boardBuilder,
			FlexTable boardTable, List<Dtos.ProjectDto> projectsOnBoard) {
		int row = 0;
		for (Dtos.ProjectDto project : projectsOnBoard) {
            ProjectHeader projectHeader = new ProjectHeader(board, project);
            boardTable.setWidget(row, 0, projectHeader);
            projectHeader.init();
			FlexTable projectTable = new ProjectGui(board, project, projectHeader);
			projectTable.setStyleName(style.board());
			projectTable.getElement().getStyle().setProperty("paddingBottom", "2px");
			projectTable.getElement().getStyle().setProperty("marginLeft", "-3px");
			boolean hasWorkflow = board.getWorkflow().getWorkflowitems().size() != 0;
			if (hasWorkflow) {
				boardBuilder.buildBoard(
                        new WipLimitGuard(),
                        new ArrayList<ExtendedWorkflowitem>(),
                        null,
                        board.getWorkflow(),
                        project,
						projectTable,
                        dragController,
                        0, 0);
				boardTable.setWidget(row, 1, projectTable);
			} else {
				boardTable.setWidth("100%");
				if (row == 0) {
					boardTable
							.setWidget(
									row,
									1,
									new NoContentWarningPanel(
											"There is no workflow on this board. Please add at least one workflowitem to this board."));
				}
			}
			row++;
		}
	}

	private void createNoBoardsPanel(BoardsPanel panel) {
		panel.addBoard(new NoContentWarningPanel(
				"There are no boards configured. Please create at lease one board."));
	}

	public void initialize(
			final ModuleInitializeCallback boardsModuleInitialized) {
		loadUsers(boardsModuleInitialized);
	}
	
	private void loadUsers(final ModuleInitializeCallback boardsModuleInitialized) {
        Dtos.SessionDto dto = DtoFactory.sessionDto(CurrentUser.getInstance().getSessionId());
        dto.setCommandName(CommandNames.GET_ALL_USERS_COMMAND.name);
        ServerCaller.<Dtos.SessionDto, Dtos.UsersDto>sendRequest(
                dto,
                Dtos.UsersDto.class,
                new ServerCallCallback<Dtos.UsersDto>() {

                    @Override
                    public void success(Dtos.UsersDto response) {
                        UsersManager.getInstance().initUsers(response.getValues());
                        loadClassesOfServices(boardsModuleInitialized);
                    }
                }
        );
	}
	
	private void loadClassesOfServices(final ModuleInitializeCallback boardsModuleInitialized) {

        Dtos.SessionDto dto = DtoFactory.sessionDto(CurrentUser.getInstance().getSessionId());
        dto.setCommandName(CommandNames.GET_ALL_CLASS_OF_SERVICE.name);

        ServerCaller.<Dtos.SessionDto, Dtos.ClassOfServicesDto>sendRequest(
                dto,
                Dtos.ClassOfServicesDto.class,
                new ServerCallCallback<Dtos.ClassOfServicesDto>() {

                    @Override
                    public void success(Dtos.ClassOfServicesDto response) {
                        ClassOfServicesManager.getInstance().setClassesOfServices(response.getValues());
                        loadBoards(boardsModuleInitialized);
                    }
                }
        );

	}
	
	private void loadBoards(final ModuleInitializeCallback boardsModuleInitialized) {
        ServerCaller.<Dtos.GetAllBoardsWithProjectsDto, Dtos.BoardsWithProjectsDto>sendRequest(
                DtoFactory.getAllBoardsWithProjectsDto(true, true),
                Dtos.BoardsWithProjectsDto.class,
                new ServerCallCallback<Dtos.BoardsWithProjectsDto>() {

                    @Override
                    public void onSuccess(Dtos.BoardsWithProjectsDto response) {
                        // overriding onSuccess to avoid hiding the progress bar - it will have to be done after the tasks are added
                        Widget boards = buildBoard(response);
                        addTasks(response);
                        boardsModuleInitialized.initialized(boards);
                    }
                }
        );
	}

}
