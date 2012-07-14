package com.googlecode.kanbanik.client.modules;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.board.BoardPanel;
import com.googlecode.kanbanik.client.components.board.ProjectHeader;
import com.googlecode.kanbanik.client.components.board.TaskAddedMessage;
import com.googlecode.kanbanik.client.components.board.TaskContainer;
import com.googlecode.kanbanik.client.components.board.TaskMovingDropController;
import com.googlecode.kanbanik.client.components.board.WorkflowitemPlace;
import com.googlecode.kanbanik.client.components.task.TaskChangedMessage;
import com.googlecode.kanbanik.client.components.task.TaskDeleteRequestedMessage;
import com.googlecode.kanbanik.client.components.task.TaskSaver;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.KanbanikModule.ModuleInitializeCallback;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2.BoardGuiBuilder;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardsModule {

	private static final BoardStyle style = KanbanikResources.INSTANCE.boardStyle();
	
	static {
		TaskSaver taskSaver = new TaskSaver();
		MessageBus.registerListener(TaskChangedMessage.class, taskSaver);
		MessageBus.registerListener(TaskDeleteRequestedMessage.class, taskSaver);
		style.ensureInjected();
	}
	
	
	private void addTasks(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		for (BoardWithProjectsDto boardWithProjects : result.getPayload().getList()) {
			for (ProjectDto project : boardWithProjects.getProjectsOnBoard()) {
				for (TaskDto task : project.getTasks()) {
					MessageBus.sendMessage(new TaskAddedMessage(task, this));
				}
			}
		}
	}
	
	private Widget buildBoard(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		BoardPanel panel = new BoardPanel();

		BoardBoardGuiBuilder boardBuilder = new BoardBoardGuiBuilder();
		
		for (BoardWithProjectsDto boardWithProjects : result.getPayload()
				.getList()) {
			BoardDto board = boardWithProjects.getBoard();

			int row = 0;
			FlexTable boardTable = new FlexTable();
			AbsolutePanel panelWithDraggabls = new AbsolutePanel();
			PickupDragController dragController = new PickupDragController(
					panelWithDraggabls, false);
			panelWithDraggabls.add(boardTable);
			for (ProjectDto project : boardWithProjects.getProjectsOnBoard()) {
				boardTable.setWidget(row, 0, new ProjectHeader(board, project));
				FlexTable projectTable = new FlexTable();
				projectTable.setStyleName(style.board());
				boardBuilder.buildBoard(board.getRootWorkflowitem(), project, projectTable, dragController,
						0, 0);
				boardTable.setWidget(row, 1, projectTable);
				row++;
			}
			panel.addBoard(panelWithDraggabls);

		}

		return panel;
	}

	public void initialize(final ModuleInitializeCallback boardsModuleInitialized) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<VoidParams, SimpleParams<ListDto<BoardWithProjectsDto>>> invokeCommand(
				ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS,
				new VoidParams(),
				new KanbanikAsyncCallback<SimpleParams<ListDto<BoardWithProjectsDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
						Widget boards = buildBoard(result);
						addTasks(result);
						boardsModuleInitialized.initialized(boards);
					}


				});
		}});
	}

	class BoardBoardGuiBuilder extends BoardGuiBuilder {

		@Override
		protected Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project) {
			TaskContainer taskContainer = new TaskContainer();
			DropController dropController = new TaskMovingDropController(taskContainer, currentItem, project);
			dragController.registerDropController(dropController);
			return taskContainer;
		}

		@Override
		protected Widget createWorkflowitemPlace(
				PickupDragController dragController,
				WorkflowitemDto currentItem, ProjectDto project,
				Widget childTable) {
			return new WorkflowitemPlace(currentItem, project, childTable, dragController);
		}
		
	}
}
