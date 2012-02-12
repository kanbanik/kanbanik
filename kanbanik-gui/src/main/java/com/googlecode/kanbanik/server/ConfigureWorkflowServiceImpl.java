package com.googlecode.kanbanik.server;

import java.util.List;

import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;


public class ConfigureWorkflowServiceImpl extends AbstractServiceServlet implements ConfigureWorkflowService {

	private static final String WORKER = "configureServiceWorker";
	private static final long serialVersionUID = -3199272488009056582L;

	public List<BoardDTO> allBoards() {
		return getWorker().allBoards();
	}

	public BoardDTO loadRealBoard(BoardDTO boardStub) {
		return getWorker().loadRealBoard(boardStub);
	}

	public List<ProjectDTO> allProjects() {
		return getWorker().allProjects();
	}

	public void addProjects(BoardDTO board, List<ProjectDTO> projects) {
		getWorker().addProjects(board, projects);
	}
	
	
	public void removeProjects(BoardDTO board, List<ProjectDTO> projects) {
		getWorker().removeProjects(board, projects);
	}

	public ProjectDTO createNewProject(ProjectDTO project) {
		return getWorker().createNewProject(project);
	}
	
	public BoardDTO createNewBoard(BoardDTO board) {
		return getWorker().createNewBoard(board);
	}
	
	public WorkflowItemDTO storeWorkflowItem(WorkflowDTO workflow, WorkflowItemDTO workfloitem) {
		return getWorker().storeWorkflowItem(workflow, workfloitem);
	}
	
	public ReturnObjectDTO deleteWorkflowItem(WorkflowDTO workflow, WorkflowItemDTO workfloitem) {
		return getWorker().deleteWorkflowItem(workflow, workfloitem);
	}

	public ReturnObjectDTO deleteBoard(BoardDTO board) {
		return getWorker().deleteBoard(board);
	}
	
	public void editBoard(BoardDTO board) {
		getWorker().editBoard(board);
	}
	
	public ReturnObjectDTO deleteProject(ProjectDTO projectDto) {
		return getWorker().deleteProject(projectDto);
	}
	
	public ReturnObjectDTO editProject(ProjectDTO projectDto) {
		return getWorker().editProject(projectDto);
	}
	
	private ConfigureWorkflowServiceWorker getWorker() {
		ConfigureWorkflowServiceWorker worker = getBean(WORKER);
		return worker;
	}


}
