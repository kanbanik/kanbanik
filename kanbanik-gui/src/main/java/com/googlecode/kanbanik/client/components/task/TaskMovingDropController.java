package com.googlecode.kanbanik.client.components.task;


import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.client.model.WorkflowItemGUI;

public class TaskMovingDropController extends VerticalPanelDropController {

	private WorkflowItemGUI workflowItem;
	
	public TaskMovingDropController(VerticalPanel dropTarget, WorkflowItemGUI workflowItem) {
		super(dropTarget);
		this.workflowItem = workflowItem;
	}
	
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			notifyDropped(widget);
		}
		super.onDrop(context);
	}

	private void notifyDropped(Widget widget) {
		if (widget instanceof TaskGui) {
			((TaskGui) widget).taskMoved(workflowItem);
			
			MessageBus.sendMessage(new TaskChangedMessage(((TaskGui) widget).getDto(), this));
		}
	}
	
}
