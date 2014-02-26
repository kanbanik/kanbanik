package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;

public class PaletteWorkflowitemWidget extends WorkflowitemWidget {

	public PaletteWorkflowitemWidget(Dtos.WorkflowitemDto workflowitem, Widget child) {
		super(workflowitem, child);
		
		editButton.setVisible(false);
		deleteButton.setVisible(false);
	}
	
	protected PaletteWorkflowitemWidget cloneWidget() {
		Dtos.WorkflowitemDto dto = DtoFactory.workflowitemDto();
		dto.setName(getWorkflowitem().getName());
		dto.setItemType(getWorkflowitem().getItemType());
		dto.setParentWorkflow(getWorkflowitem().getParentWorkflow());
		dto.setNestedWorkflow(getWorkflowitem().getNestedWorkflow());
		dto.setWipLimit(getWorkflowitem().getWipLimit());
		return new PaletteWorkflowitemWidget(dto, getChild());
	}
}
