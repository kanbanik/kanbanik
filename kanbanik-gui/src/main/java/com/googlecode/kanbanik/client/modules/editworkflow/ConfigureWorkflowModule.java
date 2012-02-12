package com.googlecode.kanbanik.client.modules.editworkflow;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.modules.KanbanikModule;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;

public class ConfigureWorkflowModule extends HorizontalPanel implements KanbanikModule {

	private final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);

	private BoardsBox boardsBox = new BoardsBox(this);

	private EditableBoard editableBoard;

	private List<ProjectDTO> projects;

	public ConfigureWorkflowModule() {
		
		setStyleName("edit-workflow-module");
		
		Panel boardsPanel = new VerticalPanel();
		boardsPanel.add(boardsBox);
		
		add(boardsPanel);
	}

	public void initialize(ModuleInitializeCallback initializedCallback) {
		add(boardsBox);
		loadProjects(new Runnable() {
			
			public void run() {
				loadBoards();
			}
		});

		setVisible(true);
		initializedCallback.initialized(this);
	}

	private void loadProjects(final Runnable callback) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.allProjects(new KanbanikAsyncCallback<List<ProjectDTO>>() {

							@Override
							public void success(List<ProjectDTO> result) {
								projects = result;
								callback.run();
							}

						});				
					}
				}
		);

	}


	private void loadBoards() {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.allBoards(new KanbanikAsyncCallback<List<BoardDTO>>() {

							@Override
							public void success(List<BoardDTO> result) {
								boardsBox.setBoards(result);
								if (result != null && result.size() > 0) {
									selectedBoardChanged(result.iterator().next());	
								}								
							}

						});				
					}
				}
		);

	}

	public void selectedBoardChanged(final BoardDTO selectedDTO) {

		if (selectedDTO == null) {
			// this means that no board is changed - e.g. the last one has been deleted
			removeEverithing();
			return;
		}
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.loadRealBoard(selectedDTO, new KanbanikAsyncCallback<BoardDTO>() {

							@Override
							public void success(BoardDTO result) {
								editBoard(result);
							}

						});				
					}
				}
		);

	}

	private Label paddingLabel = null;
	
	private void editBoard(final BoardDTO board) {
		// well, this is a hack. It should listen to ProjectAddedMessage and update the projects.
		loadProjects(new Runnable() {
			
			public void run() {
				removeEverithing();
				
				editableBoard = new EditableBoard(board, projects);
				add(editableBoard);

				paddingLabel = new Label(" ");
				paddingLabel.setWidth("100%");
				add(paddingLabel);
				
				boardsBox.editBoard(board, projects);		
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

}
