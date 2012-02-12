package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowItemPalette extends HorizontalPanel {

	private PickupDragController dragController;

	public WorkflowItemPalette(PickupDragController dragController) {
		super();
		this.dragController = dragController;
	}

	@Override
	public void add(Widget w) {
		// disable adding new stuff to this palette	
	}

	public void addWithDraggable(Widget w) {
		super.add(w);
		dragController.makeDraggable(w);	
	}
	
	@Override
	public void insert(Widget w, int beforeIndex) {
		// disable adding new stuff to this palette
	}
	
	public void insertFromRemove(Widget w, int beforeIndex) {
		super.insert(w, beforeIndex);
	}
	
	@Override
	public boolean remove(Widget w) {
		int index = getWidgetIndex(w);
		if (index != -1 && w instanceof DraggableWorkflowItem) {
			Widget clone = ((DraggableWorkflowItem) w).cloneItem();
			dragController.makeDraggable(clone);
			insertFromRemove(clone, index);
		}
		
		return super.remove(w);
	}
}
