package com.googlecode.kanbanik.client.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardRefreshRequestListener;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WorkflowEditingComponent;
import com.googlecode.kanbanik.dto.CommandNames;

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

        ServerCaller.<Dtos.GetAllBoardsWithProjectsDto, Dtos.BoardsWithProjectsDto>sendRequest(
                DtoFactory.getAllBoardsWithProjectsDto(false, false),
                Dtos.BoardsWithProjectsDto.class,
                new ServerCallCallback<Dtos.BoardsWithProjectsDto>() {

                    @Override
                    public void success(Dtos.BoardsWithProjectsDto response) {
                        List<Dtos.BoardWithProjectsDto> boards = response.getValues();
                        boardsBox.setBoards(boards);
                        if (boards.size() != 0) {
                            selectedBoardChanged(boards.iterator().next());
                        } else {
                            selectedBoardChanged(null);
                        }

                        initializedCallback.initialized(ConfigureWorkflowModule.this);
                    }
                }
        );

		setVisible(true);
		
	}

	public void selectedBoardChanged(final Dtos.BoardWithProjectsDto selectedDto) {
		if (selectedDto == null) {
			// this means that no board is changed - e.g. the last one has been deleted
			removeEverything();
		}
		
		editBoard(selectedDto);
		
		boardsBox.selectedBoardChanged(selectedDto == null ? null : selectedDto.getBoard());
	}

	private void editBoard(final Dtos.BoardWithProjectsDto boardWithProjects) {
		// well, this is a hack. It should listen to ProjectAddedMessage and update the projects.

        Dtos.SessionDto req = DtoFactory.sessionDto();
        req.setCommandName(CommandNames.GET_ALL_PROJECTS.name);

        ServerCaller.<Dtos.SessionDto, Dtos.ProjectsDto>sendRequest(
                req,
                Dtos.ProjectsDto.class,
                new ServerCallCallback<Dtos.ProjectsDto>() {

                    @Override
                    public void success(Dtos.ProjectsDto result) {
                        refreshProjectsOnBoard(boardWithProjects, result);

                        removeEverything();

                        if (boardWithProjects != null) {
                            workflowEditingComponent.initialize(boardWithProjects);
                            add(workflowEditingComponent);
                        }

                        boardsBox.editBoard(boardWithProjects, result.getValues());
                    }

                }
        );
	}

    private void refreshProjectsOnBoard(
            final Dtos.BoardWithProjectsDto boardWithProjects,
            Dtos.ProjectsDto result) {

        if (boardWithProjects == null) {
            return;
        }

        String boardId = boardWithProjects.getBoard().getId();
        List<Dtos.ProjectDto> projectsOnBoard = new ArrayList<Dtos.ProjectDto>();
        List<Dtos.ProjectDto> projectDtos = result.getValues() != null ? result.getValues() : new ArrayList<Dtos.ProjectDto>();
        for (Dtos.ProjectDto projectDto : projectDtos) {
            if (projectDto.getBoardIds() == null) {
                continue;
            }

            for (String projectOnBoardId : projectDto.getBoardIds()) {
                if (projectOnBoardId.equals(boardId)) {
                    projectsOnBoard.add(projectDto);
                    break;
                }
            }
        }

        boardWithProjects.setProjectsOnBoard(DtoFactory.projectsDto(projectsOnBoard));
    }

	private void removeEverything() {
		if (workflowEditingComponent != null) {
			remove(workflowEditingComponent);	
		}
	}

}