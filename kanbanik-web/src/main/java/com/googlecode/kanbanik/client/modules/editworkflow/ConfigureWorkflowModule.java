package com.googlecode.kanbanik.client.modules.editworkflow;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.modules.KanbanikModule;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ConfigureWorkflowModule extends HorizontalPanel implements KanbanikModule {

	private BoardsBox boardsBox = new BoardsBox(this);

	private EditableBoard editableBoard;

	public ConfigureWorkflowModule() {
		
		setStyleName("edit-workflow-module");
		
		Panel boardsPanel = new VerticalPanel();
		boardsPanel.add(boardsBox);
		
		add(boardsPanel);
	}

	public void initialize(final ModuleInitializeCallback initializedCallback) {
		add(boardsBox);
		
		
		ServerCommandInvokerManager.getInvoker().<VoidParams, SimpleParams<ListDto<BoardWithProjectsDto>>> invokeCommand(
				ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS,
				new VoidParams(),
				new KanbanikAsyncCallback<SimpleParams<ListDto<BoardWithProjectsDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
//						projects = extractProjects(result);
						List<BoardWithProjectsDto> boards = result.getPayload().getList();
						boardsBox.setBoards(boards);
						if (boards != null && boards.size() > 0) {
							selectedBoardChanged(result.getPayload().getList().iterator().next());	
						}	
						
						
						initializedCallback.initialized(ConfigureWorkflowModule.this);
					}
				});

		setVisible(true);
		
	}

	public void selectedBoardChanged(final BoardWithProjectsDto selectedDto) {

		if (selectedDto == null) {
			// this means that no board is changed - e.g. the last one has been deleted
			removeEverithing();
			return;
		}
		
		editBoard(selectedDto);
	}

	private Label paddingLabel = null;
	
	private void editBoard(final BoardWithProjectsDto boardWithProjects) {
		// well, this is a hack. It should listen to ProjectAddedMessage and update the projects.
		
		ServerCommandInvokerManager.getInvoker().<VoidParams, SimpleParams<ListDto<ProjectDto>>> invokeCommand(
				ServerCommand.GET_ALL_PROJECTS,
				new VoidParams(),
				new KanbanikAsyncCallback<SimpleParams<ListDto<ProjectDto>>>() {

					@Override
					public void success(SimpleParams<ListDto<ProjectDto>> result) {
						removeEverithing();
						
//						editableBoard = new EditableBoard(boardWithProjects, result.getPayload());
//						add(editableBoard);
//		
//						paddingLabel = new Label(" ");
//						paddingLabel.setWidth("100%");
//						add(paddingLabel);
						
						boardsBox.editBoard(boardWithProjects, result.getPayload().getList());
					}
				});
	}

	private void removeEverithing() {
		if (editableBoard != null) {
			remove(editableBoard);	
		}

		if (paddingLabel != null) {
			remove(paddingLabel);
		}
	}

	private List<ProjectDto> extractProjects(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		List<ProjectDto> projects = new ArrayList<ProjectDto>();
		for (BoardWithProjectsDto boardWithProject : result.getPayload().getList()) {
			for (ProjectDto project : boardWithProject.getProjectsOnBoard()) {
				if (!projects.contains(project)) {
					projects.add(project);
				}	
			}
			
		}
		return projects;
	}

	private List<BoardDto> extractBoards(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		List<BoardDto> boards = new ArrayList<BoardDto>();
		for (BoardWithProjectsDto boardWithProject : result.getPayload().getList()) {
			boards.add(boardWithProject.getBoard());
		}
		return boards;
	}
}
