package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class BoardPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, BoardPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Panel boardPanel;
	
	public BoardPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void addBoard(Panel board) {
		boardPanel.add(board);
	}
}
