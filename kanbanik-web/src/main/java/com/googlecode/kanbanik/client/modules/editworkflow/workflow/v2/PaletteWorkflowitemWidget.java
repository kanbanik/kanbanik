package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class PaletteWorkflowitemWidget extends WorkflowitemWidget {

	public PaletteWorkflowitemWidget(WorkflowitemDto workflowitem) {
		super(workflowitem, null);
		
		editButton.setVisible(false);
		deleteButton.setVisible(false);
	}
	
	protected PaletteWorkflowitemWidget cloneWidget() {
		WorkflowitemDto dto = new WorkflowitemDto();
		dto.setName(getWorkflowitem().getName());
		dto.setItemType(getWorkflowitem().getItemType());
		dto.setBoard(getWorkflowitem().getBoard());
		return new PaletteWorkflowitemWidget(dto);
	}
}
