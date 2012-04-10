package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public interface PanelToDTOConvertor {
	
	WorkflowItemEditPanel toPanel(WorkflowItemDTO dto);
	
	WorkflowItemDTO toDto(WorkflowItemDTO dto, WorkflowItemEditPanel panel);
}
