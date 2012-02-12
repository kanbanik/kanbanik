package com.googlecode.kanbanik.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public interface ConfigureWorkflowServiceAsync {

	void allBoards(AsyncCallback<List<BoardDTO>> callback);

	void loadRealBoard(BoardDTO boardStub, AsyncCallback<BoardDTO> callback);

	void allProjects(AsyncCallback<List<ProjectDTO>> callback);

	void addProjects(BoardDTO board, List<ProjectDTO> projects,
			AsyncCallback<Void> callback);

	void removeProjects(BoardDTO board, List<ProjectDTO> dtos, AsyncCallback<Void> callback);
	
	void createNewProject(ProjectDTO project, AsyncCallback<ProjectDTO> callback);

	void storeWorkflowItem(WorkflowDTO workflow, WorkflowItemDTO workfloitem, AsyncCallback<WorkflowItemDTO> callback);
	
	void deleteWorkflowItem(WorkflowDTO workflow, WorkflowItemDTO workfloitem, AsyncCallback<ReturnObjectDTO> callback);
	
	void createNewBoard(BoardDTO dto, AsyncCallback<BoardDTO> kanbanikAsynchCallback);

	void deleteBoard(BoardDTO board, AsyncCallback<ReturnObjectDTO> callback);

	void editBoard(BoardDTO toStore, AsyncCallback<Void> callback);

	void editProject(ProjectDTO projectDto, AsyncCallback<ReturnObjectDTO> callback);

	void deleteProject(ProjectDTO projectDto, AsyncCallback<ReturnObjectDTO> kanbanikAsyncCallback);

}
