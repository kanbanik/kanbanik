package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class BoardsDto implements KanbanikDto {
	
	private static final long serialVersionUID = 4427958498253647224L;

	private List<BoardDto> boards = new ArrayList<BoardDto>();
	
	public void addBoard(BoardDto board) {
		boards.add(board);
	}
	
	public List<BoardDto> all() {
		return boards;
	}
}
