package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class BasicPanelToDTOConvertor implements PanelToDTOConvertor {

	public WorkflowItemEditPanel toPanel(WorkflowItemDTO dto) {
		WorkflowItemEditPanel panel = new WorkflowItemEditPanel();
		panel.setName(dto.getName());
		panel.setWipLimit(dto.getWipLimit());
		return panel;
	}

	public WorkflowItemDTO toDto(WorkflowItemDTO dto, WorkflowItemEditPanel panel) {
		dto.setName(panel.getName());
		dto.setWipLimit(panel.getWipLimit());
		return dto;
	}
	
}
