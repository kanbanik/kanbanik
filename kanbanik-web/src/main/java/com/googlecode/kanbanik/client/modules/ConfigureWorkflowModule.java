package com.googlecode.kanbanik.client.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardRefreshRequestListener;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WorkflowEditingComponent;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.CommandNames;
import com.googlecode.kanbanik.dto.GetAllBoardsWithProjectsParams;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
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
			removeEverything();
		}
		
		
		
		editBoard(selectedDto);
		
		boardsBox.selectedBoardChanged(selectedDto == null ? null : selectedDto.getBoard());
	}

	private void editBoard(final BoardWithProjectsDto boardWithProjects) {
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

                        boardsBox.editBoard(boardWithProjects, result.getResult());
                    }

                }
        );
	}

    private void refreshProjectsOnBoard(
            final BoardWithProjectsDto boardWithProjects,
            Dtos.ProjectsDto result) {

        if (boardWithProjects == null) {
            return;
        }

        String boardId = boardWithProjects.getBoard().getId();
        List<Dtos.ProjectDto> projectsOnBoard = new ArrayList<Dtos.ProjectDto>();

        for (Dtos.ProjectDto projectDto : result.getResult()) {
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

        boardWithProjects.setProjects(asOldProjects(projectsOnBoard));
    }

    private List<ProjectDto> asOldProjects(List<Dtos.ProjectDto> newProjects) {
        List<ProjectDto> oldProjects = new ArrayList<ProjectDto>();

        for (Dtos.ProjectDto newProject : newProjects) {
            ProjectDto oldProject = new ProjectDto();

            oldProject.setId(newProject.getId());
            oldProject.setName(newProject.getName());
            oldProject.setVersion(newProject.getVersion());
            List<BoardDto> boards = new ArrayList<BoardDto>();
            for (String id : newProject.getBoardIds()) {
                BoardDto board = new BoardDto();
                board.setId(id);
                boards.add(board);
            }

            oldProject.setBoards(boards);
            oldProjects.add(oldProject);
        }
        return oldProjects;

    }

	private void removeEverything() {
		if (workflowEditingComponent != null) {
			remove(workflowEditingComponent);	
		}
	}

}