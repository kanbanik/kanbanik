package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.components.board.MainBoard;

public class KanbanikEntryPoint implements EntryPoint {

	public void onModuleLoad() {
		MainBoard mainBoard = new MainBoard();
		mainBoard.initializeBoard(RootPanel.get("mainSection"));
	}

	
}