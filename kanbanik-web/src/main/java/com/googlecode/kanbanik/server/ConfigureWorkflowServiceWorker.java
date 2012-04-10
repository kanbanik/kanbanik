package com.googlecode.kanbanik.server;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.googlecode.kanbanik.Board;
import com.googlecode.kanbanik.Kanbanik;
import com.googlecode.kanbanik.Project;
import com.googlecode.kanbanik.ReturnObject;
import com.googlecode.kanbanik.Workflow;
import com.googlecode.kanbanik.Workflowitem;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class ConfigureWorkflowServiceWorker {
	
	private Kanbanik kanbanik;
	
	private BoardBuilder boardBuilder;
	
	private WorkflowItemBuilder workflowItemBuilder;
	
	private WorkflowItemDTOBuilder workflowItemDTOBuilder;
	
	private ReturnObjectDtoBuilder returnObjectDtoBuilder;
	
	@PersistenceContext
	private EntityManager manager;
	
	@Transactional
	public ProjectDTO createNewProject(ProjectDTO projectDTO) {
		Project project = new Project();
		project.setName(projectDTO.getName());
		Project persistedProject = kanbanik.getProjects().store(project, manager);
		ProjectDTO persistedProjectDTO = new ProjectDTO();
		persistedProjectDTO.setId(persistedProject.getId());
		persistedProjectDTO.setName(persistedProject.getName());
		return persistedProjectDTO;
	}
	
	@Transactional
	public ReturnObjectDTO deleteProject(ProjectDTO projectDto) {
		Project project = new Project();
		project.setId(projectDto.getId());
		ReturnObject returnObject = kanbanik.getProjects().delete(project, manager);
		return returnObjectDtoBuilder.build(returnObject);
	}
	
	@Transactional
	public ReturnObjectDTO editProject(ProjectDTO projectDto) {
		Project project = new Project();
		project.setId(projectDto.getId());
		project.setName(projectDto.getName());
		kanbanik.getProjects().store(project, manager);
		return new ReturnObjectDTO(true, "");
	}
	
	@Transactional
	public BoardDTO createNewBoard(BoardDTO boardDTO) {
		Board board = new Board();
		board.setName(boardDTO.getName());
		Board persistedBoard = kanbanik.getBoards().store(board, manager);
		BoardDTO persistedBoardDTO = new BoardDTO();
		persistedBoardDTO.setId(persistedBoard.getId());
		persistedBoardDTO.setName(persistedBoard.getName());
		return persistedBoardDTO;
	}
	
	@Transactional
	public List<ProjectDTO> allProjects() {
		List<ProjectDTO> projects = new ArrayList<ProjectDTO>();
		for(Project project : kanbanik.getProjects().all()) {
			ProjectDTO dto = new ProjectDTO();
			dto.setId(project.getId());
			dto.setName(project.getName());
			projects.add(dto);
		}
		
		return projects;
	}
	
	@Transactional
	public List<BoardDTO> allBoards() {
		
		List<BoardDTO> boards = new ArrayList<BoardDTO>();
		
		for (Board board : kanbanik.getBoards().all()) {
			BoardDTO boardDTO = new BoardDTO();
			boardDTO.setName(board.getName());
			boardDTO.setId(board.getId());
			boards.add(boardDTO);
		}
		return boards;
	}

	@Transactional
	public BoardDTO loadRealBoard(BoardDTO boardStub) {
		Board board = kanbanik.getBoards().byId(boardStub.getId());
		return boardBuilder.build(board);
	}

	@Transactional
	public void addProjects(BoardDTO boardDTO, List<ProjectDTO> projectDTOs) {
		Board board = manager.find(Board.class, boardDTO.getId());
		for (ProjectDTO projectDTO : projectDTOs) {
			Project project = manager.find(Project.class, projectDTO.getId());
			board.addProject(project);
		}
	}
	
	@Transactional
	public void removeProjects(BoardDTO boardDTO, List<ProjectDTO> projectDTOs) {
		Board board = manager.find(Board.class, boardDTO.getId());
		for (ProjectDTO projectDTO : projectDTOs) {
			Project project = manager.find(Project.class, projectDTO.getId());
			board.removeProject(project);
		}
	}
	
	@Transactional
	public WorkflowItemDTO storeWorkflowItem(WorkflowDTO workflowDTO, WorkflowItemDTO workflowitemDTO) {
		Workflow workflow = manager.find(Workflow.class, workflowDTO.getId());
		Workflowitem workflowitem = workflowItemBuilder.build(workflowDTO, workflowitemDTO);
		Workflowitem item = workflow.getWorkflowitems().store(workflowitem, manager);
		return workflowItemDTOBuilder.build(item);
	}
	
	@Transactional
	public ReturnObjectDTO deleteWorkflowItem(WorkflowDTO workflowDTO, WorkflowItemDTO workflowitemDTO) {
		Workflow workflow = manager.find(Workflow.class, workflowDTO.getId());
		Workflowitem workflowitem = workflowItemBuilder.build(workflowDTO, workflowitemDTO);
		ReturnObject ret = workflow.getWorkflowitems().delete(workflowitem, manager);
		return returnObjectDtoBuilder.build(ret);
	}

	@Transactional
	public ReturnObjectDTO deleteBoard(BoardDTO boardDto) {
		Board board = new Board();
		board.setId(boardDto.getId());
		ReturnObject ret = kanbanik.getBoards().delete(board, manager);
		return returnObjectDtoBuilder.build(ret);
	}

	@Transactional
	public void editBoard(BoardDTO boardDto) {
		Board board = new Board();
		board.setId(boardDto.getId());
		board.setName(boardDto.getName());
		kanbanik.getBoards().store(board, manager);
	}

	public void setKanbanik(Kanbanik kanbanik) {
		this.kanbanik = kanbanik;
	}
	
	public void setBoardBuilder(BoardBuilder boardBuilder) {
		this.boardBuilder = boardBuilder;
	}

	public void setWorkflowItemBuilder(WorkflowItemBuilder workflowItemBuilder) {
		this.workflowItemBuilder = workflowItemBuilder;
	}

	public void setWorkflowItemDTOBuilder(
			WorkflowItemDTOBuilder workflowItemDTOBuilder) {
		this.workflowItemDTOBuilder = workflowItemDTOBuilder;
	}

	public void setReturnObjectDtoBuilder(
			ReturnObjectDtoBuilder returnObjectDtoBuilder) {
		this.returnObjectDtoBuilder = returnObjectDtoBuilder;
	}

}
