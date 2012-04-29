package com.googlecode.kanbanik.client.modules;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
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
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardsModule {
	
	static {
		TaskSaver taskSaver = new TaskSaver();
		MessageBus.registerListener(TaskChangedMessage.class, taskSaver);
		MessageBus.registerListener(TaskDeleteRequestedMessage.class, taskSaver);
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

		for (BoardWithProjectsDto boardWithProjects : result.getPayload()
				.getList()) {
			BoardDto board = boardWithProjects.getBoard();

			int row = 0;
			FlexTable table = new FlexTable();
			table.setBorderWidth(1);
			AbsolutePanel panelWithDraggabls = new AbsolutePanel();
			PickupDragController dragController = new PickupDragController(
					panelWithDraggabls, false);
			panelWithDraggabls.add(table);
			for (ProjectDto project : boardWithProjects.getProjectsOnBoard()) {
				table.setWidget(row, 0, new ProjectHeader(board, project));
				buildBoard(board.getRootWorkflowitem(), project, table, dragController,
						row, 1);
				row++;
			}
			panel.addBoard(panelWithDraggabls);

		}

		return panel;
	}

	private void buildBoard(
			WorkflowitemDto workflowitem,
			ProjectDto project,
			FlexTable table,
			PickupDragController dragController, 
			int row, 
			int column) {
		if (workflowitem == null) {
			return;
		}
		WorkflowitemDto currentItem = workflowitem;

		while (true) {
			if (currentItem.getChild() != null) {
				FlexTable childTable = new FlexTable();
				childTable.setBorderWidth(1);

				table.setWidget(row, column, new WorkflowitemPlace(currentItem, project, childTable, dragController));
				buildBoard(currentItem.getChild(), project, childTable, dragController, 0, 0);
			} else {

				TaskContainer taskContainer = createTaskContainer(dragController, currentItem, project);
				table.setWidget(row, column, new WorkflowitemPlace(currentItem, project, taskContainer, dragController));
			}

			if (currentItem.getItemType() == ItemType.VERTICAL) {
				column++;
			} else if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}

			currentItem = currentItem.getNextItem();
			if (currentItem == null) {
				break;
			}

		}

	}

	private TaskContainer createTaskContainer(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project) {
		TaskContainer taskContainer = new TaskContainer();
		DropController dropController = new TaskMovingDropController(taskContainer, currentItem, project);
		dragController.registerDropController(dropController);
		return taskContainer;
	}

	public void initialize(final ModuleInitializeCallback boardsModuleInitialized) {
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
	}

}
