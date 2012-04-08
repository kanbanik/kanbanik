package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDto implements KanbanikDto {
	
	private static final long serialVersionUID = 3986919835518304209L;

	private String name;
	
	private String id;
	
	private List<BoardDto> boards = new ArrayList<BoardDto>();
	
	private List<TaskDto> tasks = new ArrayList<TaskDto>();

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
}
