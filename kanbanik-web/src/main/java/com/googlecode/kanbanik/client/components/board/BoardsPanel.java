package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class BoardsPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, BoardsPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Panel boardPanel;
	
	public BoardsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void addBoard(Composite board) {
		boardPanel.add(board);
	}
}
