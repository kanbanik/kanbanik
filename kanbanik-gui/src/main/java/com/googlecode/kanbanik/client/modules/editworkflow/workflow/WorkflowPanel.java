package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowPanel extends HorizontalPanel {
	
	private PickupDragController dragController;
	
	private EditableWorkflowItemBuilder builder;
	
	public WorkflowPanel(PickupDragController dragController, EditableWorkflowItemBuilder builder) {
		super();
		this.dragController = dragController;
		this.builder = builder;
	}
	
	@Override
	public void add(Widget w) {
		Widget editableWidget = builder.toEditableItem(w);
		super.add(editableWidget);
		makeDraggable(editableWidget);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		Widget editableWidget = builder.toEditableItem(w);
		super.insert(editableWidget, beforeIndex);
		makeDraggable(editableWidget);
	}

	private void makeDraggable(Widget w) {
		if (w instanceof EditableWorkflowItem) {
			dragController.makeDraggable(w, ((EditableWorkflowItem) w).getHeader());	
		}
	}
	
	
}
