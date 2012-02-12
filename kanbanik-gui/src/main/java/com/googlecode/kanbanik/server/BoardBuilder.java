package com.googlecode.kanbanik.server;

import com.googlecode.kanbanik.Board;
import com.googlecode.kanbanik.Project;
import com.googlecode.kanbanik.Workflowitem;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;


public class BoardBuilder {
	
	private WorkflowItemDTOBuilder workflowItemBuilder;
	
	private ProjectBuilder projectBuilder;
	
	public BoardDTO build(Board board) {
		BoardDTO boardDTO = new BoardDTO();
		WorkflowDTO workflowDTO = new WorkflowDTO();
		workflowDTO.setId(board.getWorkflow().getId());
		
		for (Workflowitem workflowitem : board.getWorkflow().getWorkflowitems()) {
			if (!workflowItemBuilder.toAddToDTO(workflowitem)) {
				continue;
			}
			
			workflowDTO.addWorkflowItem(workflowItemBuilder.build(workflowitem));
		}
		
		boardDTO.setWorkflow(workflowDTO);
		
		for (Project project : board.getProjects().all()) {
			boardDTO.addProject(projectBuilder.build(project, boardDTO));
		}
		
		boardDTO.setId(board.getId());
		boardDTO.setName(board.getName());
		return boardDTO;
	}

	public void setWorkflowItemBuilder(WorkflowItemDTOBuilder workflowItemBuilder) {
		this.workflowItemBuilder = workflowItemBuilder;
	}

	public void setProjectBuilder(ProjectBuilder projectBuilder) {
		this.projectBuilder = projectBuilder;
	}
}
