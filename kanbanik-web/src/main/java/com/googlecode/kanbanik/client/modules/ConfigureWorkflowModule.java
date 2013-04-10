package com.googlecode.kanbanik.client.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardRefreshRequestListener;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WorkflowEditingComponent;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.GetAllBoardsWithProjectsParams;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ConfigureWorkflowModule extends HorizontalPanel implements KanbanikModule {

	private BoardsBox boardsBox = new BoardsBox(this);

	private static final WorkflowEditingComponent workflowEditingComponent = new WorkflowEditingComponent();;

	public ConfigureWorkflowModule() {
		setStyleName("edit-workflow-module");
		
		// register static listeners
		MessageBus.registerListener(BoardsRefreshRequestMessage.class, new BoardRefreshRequestListener());
	}

	public void initialize(final ModuleInitializeCallback initializedCallback) {
		add(boardsBox);
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<GetAllBoardsWithProjectsParams, SimpleParams<ListDto<BoardWithProjectsDto>>> invokeCommand(
				ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS,
				new GetAllBoardsWithProjectsParams(false),
				new BaseAsyncCallback<SimpleParams<ListDto<BoardWithProjectsDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
						List<BoardWithProjectsDto> boards = result.getPayload().getList();
						boardsBox.setBoards(boards);
						if (boards.size() != 0) {
							selectedBoardChanged(boards.iterator().next());	
						} else {
							selectedBoardChanged(null);
						}
						
						initializedCallback.initialized(ConfigureWorkflowModule.this);
					}
				});
					}});

		setVisible(true);
		
	}

	public void selectedBoardChanged(final BoardWithProjectsDto selectedDto) {

		if (selectedDto == null) {
			// this means that no board is changed - e.g. the last one has been deleted
			removeEverithing();
		}
		
		editBoard(selectedDto);
	}

	private void editBoard(final BoardWithProjectsDto boardWithProjects) {
		// well, this is a hack. It should listen to ProjectAddedMessage and update the projects.
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<VoidParams, SimpleParams<ListDto<ProjectDto>>> invokeCommand(
				ServerCommand.GET_ALL_PROJECTS,
				new VoidParams(),
				new BaseAsyncCallback<SimpleParams<ListDto<ProjectDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<ProjectDto>> result) {
						refreshProjectsOnBoard(boardWithProjects, result);
						
						removeEverithing();
						
						if (boardWithProjects != null) {
							workflowEditingComponent.initialize(boardWithProjects);
							add(workflowEditingComponent);
						}

						boardsBox.editBoard(boardWithProjects, result.getPayload().getList());
					}

					private void refreshProjectsOnBoard(
							final BoardWithProjectsDto boardWithProjects,
							SimpleParams<ListDto<ProjectDto>> result) {
						
						if (boardWithProjects == null) {
							return;
						}
						
						String boardId = boardWithProjects.getBoard().getId();
						List<ProjectDto> projectsOnBoard = new ArrayList<ProjectDto>();
						
						for (ProjectDto projectDto : result.getPayload().getList()) {
							for (BoardDto boardDto : projectDto.getBoards()) {
								if (boardDto.getId().equals(boardId)) {
									projectsOnBoard.add(projectDto);
									break;
								}
							}
						}
						
						boardWithProjects.setProjects(projectsOnBoard);
					}
				});
		}});
	}

	private void removeEverithing() {
		if (workflowEditingComponent != null) {
			remove(workflowEditingComponent);	
		}
	}

}