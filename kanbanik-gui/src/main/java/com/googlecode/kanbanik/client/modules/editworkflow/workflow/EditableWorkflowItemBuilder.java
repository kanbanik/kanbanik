package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.shared.WorkflowDTO;

public class EditableWorkflowItemBuilder {
	
	private WorkflowDTO workflowDTO;
	
	public EditableWorkflowItemBuilder(WorkflowDTO workflowDTO) {
		super();
		this.workflowDTO = workflowDTO;
	}

	public Widget toEditableItem(Widget w) {
		if (w instanceof EditableWorkflowItem) {
			return (EditableWorkflowItem) w;
		} else if (w instanceof DraggableWorkflowItem) {
			DraggableWorkflowItem item = (DraggableWorkflowItem) w;
			WorkflowItemEditComponent editComponent = new WorkflowItemEditComponent(workflowDTO, item, new BasicPanelToDTOConvertor());
			WorkflowitemDeletingComponent deleteComponent = new WorkflowitemDeletingComponent(workflowDTO, item);
			return new EditableWorkflowItem((DraggableWorkflowItem) w, editComponent, deleteComponent);
		}
		
		return w;
	}
}
