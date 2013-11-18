package com.googlecode.kanbanik.dto.shell;

import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.TaskDto;

public class MoveTaskParams implements Params {

	private static final long serialVersionUID = 1646610147255479499L;

	private TaskDto task;

	private ProjectDto project;

	private String prevOrder;

	private String nextOrder;

    private String sessionId;

    public MoveTaskParams() {
	}

	public MoveTaskParams(TaskDto task, ProjectDto project, String prevOrder, String nextOrder) {
		super();
		this.task = task;
		this.project = project;
		this.prevOrder = prevOrder;
		this.nextOrder = nextOrder;
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

	public String getPrevOrder() {
		return prevOrder;
	}

	public void setPrevOrder(String prevOrder) {
		this.prevOrder = prevOrder;
	}

	public String getNextOrder() {
		return nextOrder;
	}

	public void setNextOrder(String nextOrder) {
		this.nextOrder = nextOrder;
	}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
