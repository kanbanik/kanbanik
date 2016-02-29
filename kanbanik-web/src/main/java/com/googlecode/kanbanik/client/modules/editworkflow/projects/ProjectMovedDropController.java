package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.ErrorDialog;

public class ProjectMovedDropController extends FlowPanelDropController {
	private WidgetsDropListener listener;

	private String dropPosition;

	private String message;

	private boolean enabled;
	
	public ProjectMovedDropController(FlowPanel dropTarget, String dropPosition, WidgetsDropListener listener) {
		super(dropTarget);
		this.listener = listener;
		this.dropPosition = dropPosition;
		enabled = true;
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		if (!enabled) {
			new ErrorDialog(message).center();
			super.onDrop(context);
			throw new VetoDragException();
		}
		
		super.onPreviewDrop(context);
	}
	
	public void onDrop(DragContext context) {
		List<Widget> toDrop = new ArrayList<>();
		for (Widget widget : context.selectedWidgets) {
			if (widget instanceof ProjectWidget) {
				ProjectWidget projectWidget = (ProjectWidget) widget;
				if (projectWidget.isNewPosition(dropPosition)) {
					toDrop.add(widget);
					projectWidget.setPosition(dropPosition);
				}
			}
		}
		listener.dropped(toDrop);
		super.onDrop(context);
	}
	
	public void disableDrop(String message) {
		this.message = message;
		this.enabled = false;
	}
}
