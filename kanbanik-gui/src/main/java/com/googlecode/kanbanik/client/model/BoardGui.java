package com.googlecode.kanbanik.client.model;

import java.util.Map;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.TaskDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;


public class BoardGui extends VerticalPanel {

	private BoardDTO boardDTO;
	
	public BoardGui(BoardDTO boardDTO, WorkflowGUI workflow, Map<WorkflowItemPlaceDTO, WorkflowItemGUI> dtoPlaceToPlace) {
		this.boardDTO = boardDTO;
		setStyleName("board-gui");
		HorizontalPanel header = new HorizontalPanel();
		HorizontalPanel paddingPanel = new HorizontalPanel();
		paddingPanel.setStyleName("board-name");
		paddingPanel.add(new Label(" "));
		header.add(paddingPanel);
		for (CompositeWorkflowItemGUI item : workflow.getItems()) {
			header.add(item);
		}
		
		add(header);
		
		initProjects(boardDTO, workflow, dtoPlaceToPlace);
	}

	private void initProjects(BoardDTO boardDTO, WorkflowGUI workflow, Map<WorkflowItemPlaceDTO, WorkflowItemGUI> dtoPlaceToPlace) {
		for (ProjectDTO project : boardDTO.getProjects()) {
			if (workflow.getWorkflowItems() == null || workflow.getWorkflowItems().size() == 0) {
				continue;
			}
			
			ProjectGUI projectGui = new ProjectGUI(project, workflow);
			for (TaskDTO taskDTO : project.getTasks()) {
				WorkflowItemGUI workflowItemGui = dtoPlaceToPlace.get(taskDTO.getPlace());
				if (workflowItemGui == null) {
					// this means that this specific task is defined for this project but on a different workflow 
					// it means that this project is present on different boards, and this task is for this project from a different board
					continue;
				}
				projectGui.addTask(new TaskGui(taskDTO), workflowItemGui);
			}
			addProject(projectGui);
		}
	}
	
	public void addProject(ProjectGUI project) {
		
		add(project);
	}

	public BoardDTO getBoardDTO() {
		return boardDTO;
	}
	
}
