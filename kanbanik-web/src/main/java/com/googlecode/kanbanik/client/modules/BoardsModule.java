package com.googlecode.kanbanik.client.modules;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.*;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.board.*;
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
import com.googlecode.kanbanik.dto.*;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

import java.util.List;

public class BoardsModule {

	private static final BoardStyle style = KanbanikResources.INSTANCE
			.boardStyle();

	static {
		style.ensureInjected();
		DeleteKeyListener.INSTANCE.initialize();
		new DeleteTasksMessageListener().initialize();
	}

	private void addTasks(final SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		for (BoardWithProjectsDto boardWithProjects : result.getPayload().getList()) {
			for (TaskDto task : boardWithProjects.getBoard().getTasks()) {
				MessageBus.sendMessage(new TaskAddedMessage(task, this));
			}
		}
	}

	private Widget buildBoard(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		BoardsPanel panel = new BoardsPanel();
		panel.getElement().setId("boards");

		List<BoardWithProjectsDto> boardsWithProjects = result.getPayload()
				.getList();

		if (boardsWithProjects == null || boardsWithProjects.size() == 0) {
			createNoBoardsPanel(panel);
		} else {
			createBoardsPanel(panel, boardsWithProjects);
		}

		return panel;
	}

	private void createBoardsPanel(BoardsPanel panel,
			List<BoardWithProjectsDto> boardsWithProjects) {
		BoardBoardGuiBuilder boardBuilder = new BoardBoardGuiBuilder();
		for (BoardWithProjectsDto boardWithProjects : boardsWithProjects) {
			BoardDto board = boardWithProjects.getBoard();

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
			List<ProjectDto> projectsOnBoard = boardWithProjects
					.getProjectsOnBoard();
			if (projectsOnBoard == null || projectsOnBoard.size() == 0) {
				addNoProjects(boardTable);
			} else {
				addProjcets(dragController, board, boardBuilder, boardTable,
						projectsOnBoard);
			}
			panel.addBoard(new BoardPanel(board.getName(), panelWithDraggabls));

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
			BoardDto board, BoardBoardGuiBuilder boardBuilder,
			FlexTable boardTable, List<ProjectDto> projectsOnBoard) {
		int row = 0;
		for (ProjectDto project : projectsOnBoard) {
			boardTable.setWidget(row, 0, new ProjectHeader(board, project));
			FlexTable projectTable = new FlexTable();
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
                        UsersManager.getInstance().initUsers(response.getResult());
                        loadClassesOfServices(boardsModuleInitialized);
                    }
                }
        );
	}
	
	private void loadClassesOfServices(final ModuleInitializeCallback boardsModuleInitialized) {
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				ServerCommandInvokerManager
						.getInvoker()
						.<VoidParams, SimpleParams<ListDto<ClassOfServiceDto>>> invokeCommand(
								ServerCommand.GET_ALL_CLASS_OF_SERVICES,
								new VoidParams(),
								new BaseAsyncCallback<SimpleParams<ListDto<ClassOfServiceDto>>>() {

									@Override
									public void success(SimpleParams<ListDto<ClassOfServiceDto>> result) {
										ClassOfServicesManager.getInstance().setClassesOfServices(result.getPayload().getList());
										loadBoards(boardsModuleInitialized);
									}

								});
			}
		});
		

	}
	
	private void loadBoards(final ModuleInitializeCallback boardsModuleInitialized) {
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				ServerCommandInvokerManager
						.getInvoker()
						.<GetAllBoardsWithProjectsParams, SimpleParams<ListDto<BoardWithProjectsDto>>> invokeCommand(
								ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS,
								new GetAllBoardsWithProjectsParams(true),
								new BaseAsyncCallback<SimpleParams<ListDto<BoardWithProjectsDto>>>() {

									@Override
									public void success(
											SimpleParams<ListDto<BoardWithProjectsDto>> result) {
										Widget boards = buildBoard(result);
										addTasks(result);
										boardsModuleInitialized.initialized(boards);
									}



								});
			}
		});
	}
	
	class BoardBoardGuiBuilder extends BoardGuiBuilder {

		@Override
		protected Widget createWorkflowitemPlaceContentWidget(
				PickupDragController dragController,
				WorkflowitemDto currentItem, ProjectDto project, BoardDto board) {
			TaskContainer taskContainer = new TaskContainer(board, currentItem);
			DropController dropController = new TaskMovingDropController(
					taskContainer, currentItem, project);
			dragController.registerDropController(dropController);
			return taskContainer;
		}

		@Override
		protected Widget createWorkflowitemPlace(
				PickupDragController dragController,
				WorkflowitemDto currentItem, ProjectDto project,
				Widget childTable) {
			return new WorkflowitemPlace(currentItem, project, childTable,
					dragController);
		}

	}
}
