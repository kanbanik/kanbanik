package com.googlecode.kanbanik.client.modules;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.board.BoardPanel;
import com.googlecode.kanbanik.client.components.board.BoardsPanel;
import com.googlecode.kanbanik.client.components.board.NoContentWarningPanel;
import com.googlecode.kanbanik.client.components.board.ProjectHeader;
import com.googlecode.kanbanik.client.components.board.TaskContainer;
import com.googlecode.kanbanik.client.components.board.TaskMovingDropController;
import com.googlecode.kanbanik.client.components.board.WorkflowitemPlace;
import com.googlecode.kanbanik.client.components.task.DeleteKeyListener;
import com.googlecode.kanbanik.client.components.task.DeleteTasksMessageListener;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.modules.KanbanikModule.ModuleInitializeCallback;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardGuiBuilder;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.List;

public class BoardsModule {

	private static final BoardStyle style = KanbanikResources.INSTANCE
			.boardStyle();

	static {
		style.ensureInjected();
		DeleteKeyListener.INSTANCE.initialize();
		new DeleteTasksMessageListener().initialize();
	}

	private void addTasks(final Dtos.BoardsWithProjectsDto result) {
		for (Dtos.BoardWithProjectsDto boardWithProjects : result.getValues()) {
			for (Dtos.TaskDto task : boardWithProjects.getBoard().getTasks()) {
				MessageBus.sendMessage(new TaskAddedMessage(task, this));
			}
		}
	}

	private Widget buildBoard(Dtos.BoardsWithProjectsDto result) {
		BoardsPanel panel = new BoardsPanel();
		panel.getElement().setId("boards");

		List<Dtos.BoardWithProjectsDto> boardsWithProjects = result.getValues();

		if (boardsWithProjects == null || boardsWithProjects.size() == 0) {
			createNoBoardsPanel(panel);
		} else {
			createBoardsPanel(panel, boardsWithProjects);
		}

		return panel;
	}

	private void createBoardsPanel(BoardsPanel panel,
			List<Dtos.BoardWithProjectsDto> boardsWithProjects) {
		BoardBoardGuiBuilder boardBuilder = new BoardBoardGuiBuilder();
		for (Dtos.BoardWithProjectsDto boardWithProjects : boardsWithProjects) {
			Dtos.BoardDto board = boardWithProjects.getBoard();

			FlexTable boardTable = new FlexTable();
			AbsolutePanel panelWithDraggabls = new AbsolutePanel();
			panelWithDraggabls.addDomHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
				}
			}, ClickEvent.getType());
			
			PickupDragController dragController = new PickupDragController(
					panelWithDraggabls, false);
			panelWithDraggabls.add(boardTable);
			List<Dtos.ProjectDto> projectsOnBoard = boardWithProjects.getProjectsOnBoard() != null ? boardWithProjects.getProjectsOnBoard().getValues() : null;
			if (projectsOnBoard == null || projectsOnBoard.size() == 0) {
				addNoProjects(boardTable);
			} else {
				addProjcets(dragController, board, boardBuilder, boardTable, projectsOnBoard);
			}

			panel.addBoard(new BoardPanel(panelWithDraggabls, board));

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
			Dtos.BoardDto board, BoardBoardGuiBuilder boardBuilder,
			FlexTable boardTable, List<Dtos.ProjectDto> projectsOnBoard) {
		int row = 0;
		for (Dtos.ProjectDto project : projectsOnBoard) {
			boardTable.setWidget(row, 0, new ProjectHeader(board, project));
			FlexTable projectTable = new ProjectGui(board, project);
			projectTable.setStyleName(style.board());
			boolean hasWorkflow = board.getWorkflow().getWorkflowitems().size() != 0;
			if (hasWorkflow) {
				boardBuilder.buildBoard(board.getWorkflow(), project,
						projectTable, dragController, 0, 0);
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
                DtoFactory.getAllBoardsWithProjectsDto(true),
                Dtos.BoardsWithProjectsDto.class,
                new ServerCallCallback<Dtos.BoardsWithProjectsDto>() {

                    @Override
                    public void success(Dtos.BoardsWithProjectsDto response) {
                        Widget boards = buildBoard(response);
                        addTasks(response);
                        boardsModuleInitialized.initialized(boards);
                    }
                }
        );
	}

    class BoardBoardGuiBuilder extends BoardGuiBuilder {

		@Override
		protected Widget createWorkflowitemPlaceContentWidget(
				PickupDragController dragController,
				Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Dtos.BoardDto board) {
			TaskContainer taskContainer = new TaskContainer(board, currentItem);
			DropController dropController = new TaskMovingDropController(
					taskContainer, currentItem, project);
			dragController.registerDropController(dropController);
			return taskContainer;
		}

		@Override
		protected Widget createWorkflowitemPlace(
                PickupDragController dragController,
                Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project,
                Widget childTable, Dtos.BoardDto board) {
			return new WorkflowitemPlace(currentItem, project, childTable,
					dragController, board);
		}

	}
}
