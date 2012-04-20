package com.googlecode.kanbanik.dto.shell;

import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;

public class MoveTaskParams implements Params {
	
	private static final long serialVersionUID = 1646610147255479499L;

	private TaskDto task;
	
	private ProjectDto project;

	public MoveTaskParams() {
	}

	public MoveTaskParams(TaskDto task, ProjectDto project) {
		super();
		this.task = task;
		this.project = project;
	}

	public TaskDto getTask() {
		return task;
	}

	public void setTask(TaskDto task) {
		this.task = task;
	}

	public ProjectDto getProject() {
		return project;
	}

	public void setProject(ProjectDto project) {
		this.project = project;
	}
	
	
}
