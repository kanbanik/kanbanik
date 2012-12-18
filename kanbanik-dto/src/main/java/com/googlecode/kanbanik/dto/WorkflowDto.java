package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class WorkflowDto implements IdentifiableDto {

	private static final long serialVersionUID = 1343045359919670502L;
	
	private String id;
	
	private List<WorkflowitemDto> workflowitems = new ArrayList<WorkflowitemDto>();
	
	private BoardDto board;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<WorkflowitemDto> getWorkflowitems() {
		return workflowitems;
	}

	public void setWorkflowitems(List<WorkflowitemDto> workflowitems) {
		this.workflowitems = workflowitems;
	}

	public BoardDto getBoard() {
		return board;
	}

	public void setBoard(BoardDto board) {
		this.board = board;
	}
	
}
