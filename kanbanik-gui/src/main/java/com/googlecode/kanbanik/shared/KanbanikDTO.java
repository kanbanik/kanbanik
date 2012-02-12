package com.googlecode.kanbanik.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KanbanikDTO implements Serializable {

	private static final long serialVersionUID = -7022115830961332120L;

	private List<BoardDTO> boards = new ArrayList<BoardDTO>();

	public List<BoardDTO> getBoards() {
		return boards;
	}

	public void addBoard(BoardDTO board) {
		if (boards == null) {
			boards = new ArrayList<BoardDTO>();
		}
		boards.add(board);
	}
}
