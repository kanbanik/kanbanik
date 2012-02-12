package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public interface DraggableWorkflowItem {
	Widget cloneItem();
	void setNextItemsId(int id);
	int getId();
	void setId(int id);
	WorkflowItemDTO getDTO();
	void refreshDTO(WorkflowItemDTO dto);
}
