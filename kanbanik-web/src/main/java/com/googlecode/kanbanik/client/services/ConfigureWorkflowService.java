package com.googlecode.kanbanik.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

@RemoteServiceRelativePath("configureWorkflow")
public interface ConfigureWorkflowService extends RemoteService {
	
	List<BoardDTO> allBoards();
	
	BoardDTO loadRealBoard(BoardDTO boardStub);
	
	List<ProjectDTO> allProjects();
	
	void addProjects(BoardDTO board, List<ProjectDTO> projects);

	void removeProjects(BoardDTO board, List<ProjectDTO> dtos);

	ReturnObjectDTO editProject(ProjectDTO projectDto);

	ReturnObjectDTO deleteProject(ProjectDTO projectDto);
	
	ProjectDTO createNewProject(ProjectDTO project);
	
	BoardDTO createNewBoard(BoardDTO board);
	
	WorkflowItemDTO storeWorkflowItem(WorkflowDTO workflow, WorkflowItemDTO workfloitem);
	
	ReturnObjectDTO deleteWorkflowItem(WorkflowDTO workflow, WorkflowItemDTO workfloitem);
	
	ReturnObjectDTO deleteBoard(BoardDTO board);

	void editBoard(BoardDTO toStore);
	
}
