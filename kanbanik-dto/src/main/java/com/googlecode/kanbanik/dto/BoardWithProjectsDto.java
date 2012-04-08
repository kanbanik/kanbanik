package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class BoardWithProjectsDto implements KanbanikDto {

	private static final long serialVersionUID = 5966458801754061165L;

	private BoardDto board;
	
	private List<ProjectDto> projectsOnBoard = new ArrayList<ProjectDto>();
	
	public BoardWithProjectsDto() {
	}

	public BoardWithProjectsDto(BoardDto board) {
		this.board = board;
	}

	public BoardDto getBoard() {
		return board;
	}
	
	public List<ProjectDto> getProjectsOnBoard() {
		return projectsOnBoard;
	}
	
	public void addProject(ProjectDto project) {
		projectsOnBoard.add(project);
	}
	
}
