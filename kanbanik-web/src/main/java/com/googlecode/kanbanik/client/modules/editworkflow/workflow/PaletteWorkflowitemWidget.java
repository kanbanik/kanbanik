package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class PaletteWorkflowitemWidget extends WorkflowitemWidget {

	public PaletteWorkflowitemWidget(WorkflowitemDto workflowitem, Widget child) {
		super(workflowitem, child);
		
		editButton.setVisible(false);
		deleteButton.setVisible(false);
	}
	
	protected PaletteWorkflowitemWidget cloneWidget() {
		WorkflowitemDto dto = new WorkflowitemDto();
		dto.setName(getWorkflowitem().getName());
		dto.setItemType(getWorkflowitem().getItemType());
		dto.setBoard(getWorkflowitem().getBoard());
		dto.setWipLimit(getWorkflowitem().getWipLimit());
		return new PaletteWorkflowitemWidget(dto, getChild());
	}
}
