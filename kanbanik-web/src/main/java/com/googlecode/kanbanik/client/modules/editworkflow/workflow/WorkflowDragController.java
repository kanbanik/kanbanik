package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowDragController extends PickupDragController {

	private List<WorkflowDragControllerListener> listeners;

	public WorkflowDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	}

	@Override
	public void dragStart() {

		if (listeners == null) {
			super.dragStart();
			return;
		}
		
		Widget draggableWidget = context.draggable;
		Widget parentPanel = draggableWidget.getParent();

		if (parentPanel instanceof InsertPanel) {
			int draggableWidgetIndex = ((InsertPanel) parentPanel).getWidgetIndex(draggableWidget);
			for (WorkflowDragControllerListener listener : listeners) {
				listener.dragStart(draggableWidget, (InsertPanel) parentPanel, draggableWidgetIndex);
			}
		} else {
			throw new IllegalStateException("Only the insert panel is supported");
		}
		super.dragStart();
	}

	public void addListener(WorkflowDragControllerListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<WorkflowDragControllerListener>();
		}
		
		listeners.add(listener);
	}
	
	public static interface WorkflowDragControllerListener {
		void dragStart(Widget draggable, InsertPanel panel, int index);
	}
}
