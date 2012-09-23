package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDto implements KanbanikDto {
	
	private static final long serialVersionUID = 3986919835518304209L;

	private String name;
	
	private String id;
	
	private List<BoardDto> boards = new ArrayList<BoardDto>();
	
	private List<TaskDto> tasks = new ArrayList<TaskDto>();

	private int version;
	
	public ProjectDto() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<BoardDto> getBoards() {
		return boards;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<TaskDto> getTasks() {
		return tasks;
	}

	public void addTask(TaskDto task) {
		tasks.add(task);
	}
	
	public void addBoard(BoardDto board) {
		boards.add(board);
	}

	public void setBoards(List<BoardDto> boards) {
		this.boards = boards;
	}

	public void setTasks(List<TaskDto> tasks) {
		this.tasks = tasks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectDto other = (ProjectDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
