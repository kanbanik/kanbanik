package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;

public class TaskMovingDropController extends FlowPanelDropController {

	public TaskMovingDropController(FlowPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

	}

}
