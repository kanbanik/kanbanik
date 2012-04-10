package com.googlecode.kanbanik.client.model;

import java.util.Arrays;
import java.util.List;

import com.googlecode.kanbanik.shared.WorkflowItemDTO;


public class CompositeRegularItemGUI extends CompositeWorkflowItemGUI {

	WorkflowItemGUI IN_PROGRESS;
	
	public CompositeRegularItemGUI(WorkflowItemDTO dto, WorkflowItemGUI inProgress) {
		super(dto);
		inProgress.setCssName("task-holder");
		IN_PROGRESS = inProgress;
	}

	@Override
	public List<WorkflowItemGUI> getPlaceDescriptors() {
		return Arrays.asList(IN_PROGRESS);
	}

}
