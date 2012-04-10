package com.googlecode.kanbanik.client.model;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class QueuedItemGUI extends CompositeWorkflowItemGUI {

	WorkflowItemGUI IN_PROGRESS;
	WorkflowItemGUI DONE;
	
	public QueuedItemGUI(WorkflowItemDTO dto, WorkflowItemGUI inProgress, WorkflowItemGUI done) {
		super(dto);
		
		HorizontalPanel subheader = new HorizontalPanel();
		subheader.setStyleName("board-subheader");
		subheader.add(createCenteredLabel("In progress"));
		subheader.add(createCenteredLabel("Done"));
		add(subheader);
		
		inProgress.setCssName("sub-task-holder");
		done.setCssName("sub-task-holder");
		IN_PROGRESS = inProgress;
		DONE = done;
	}

	@Override
	public List<WorkflowItemGUI> getPlaceDescriptors() {
		return Arrays.asList(IN_PROGRESS, DONE);
	}
	
}
