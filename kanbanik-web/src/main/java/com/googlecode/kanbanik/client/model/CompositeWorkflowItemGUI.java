package com.googlecode.kanbanik.client.model;

import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public abstract class CompositeWorkflowItemGUI extends VerticalPanel {
	
	private WorkflowItemDTO dto;
	
	public CompositeWorkflowItemGUI(WorkflowItemDTO dto) {
		super();
		
		this.dto = dto;
		setStyleName("board-header");
		add(createCenteredLabel(" " + dto.getName()));
		
		if (dto.hasWipLimit()) {
			add(createCenteredLabel("(" + dto.getWipLimit() + ")"));
		}
	}
	
	public WorkflowItemDTO getDto() {
		return dto;
	}
	
	protected Label createCenteredLabel(String text) {
		Label label = new Label(text);
		label.setStyleName("centered-label");
		return label;
	}

	public abstract List<WorkflowItemGUI> getPlaceDescriptors();
}
