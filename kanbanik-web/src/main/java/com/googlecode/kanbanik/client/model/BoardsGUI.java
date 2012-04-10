package com.googlecode.kanbanik.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BoardsGUI extends VerticalPanel {
	
	private List<BoardGui> boards = new ArrayList<BoardGui>();
	
	public void addBoard(BoardGui board) {
		setStyleName("boards-gui");
		SimplePanel panel = new SimplePanel();
		panel.setStyleName("board-title");
		Label name = new Label(board.getBoardDTO().getName());
		panel.add(name);
		add(panel);
		add(board);
		boards.add(board);
	}
	
	public List<BoardGui> getBoards() {
		return boards;
	}
	
}