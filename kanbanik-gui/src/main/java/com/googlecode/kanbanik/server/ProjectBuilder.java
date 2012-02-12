package com.googlecode.kanbanik.server;

import com.googlecode.kanbanik.Project;
import com.googlecode.kanbanik.Task;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public class ProjectBuilder {
	
	private TaskBuilder taskBuilder;
	
	public ProjectDTO build(Project project, BoardDTO board) {
		ProjectDTO dto = new ProjectDTO();
		dto.setName(project.getName());
		dto.setId(project.getId());
		for (Task task : project.getTasks()) {
			
			TaskDTO taskDTO = taskBuilder.build(task, board.getWorkflow().placeById(task.getWorkflowitem().getId()));
			dto.addTask(taskDTO);
		}
		
		for (TaskDTO task : dto.getTasks()) {
			task.setProject(dto);
		}
		
		return dto;
	}

	public void setTaskBuilder(TaskBuilder taskBuilder) {
		this.taskBuilder = taskBuilder;
	}

}
