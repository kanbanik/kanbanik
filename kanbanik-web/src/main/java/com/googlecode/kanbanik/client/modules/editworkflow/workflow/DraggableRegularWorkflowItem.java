package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.model.CompositeRegularItemGUI;
import com.googlecode.kanbanik.client.model.WorkflowItemGUI;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class DraggableRegularWorkflowItem extends FocusPanel implements DraggableWorkflowItem {

	private WorkflowItemDTO dto;
	
	private WorkflowItemGUI inProgress;
	
	private Widget itemPanel;
	
	public DraggableRegularWorkflowItem(WorkflowItemDTO dto, WorkflowItemGUI inProgress) {
		setStyleName("draggable-editable-workflow-item");
		this.dto = dto;
		this.inProgress = inProgress;
		addItem();
		setTitle(dto.getName());
	}

	private void addItem() {
		itemPanel = new CompositeRegularItemGUI(dto, inProgress);
		add(itemPanel);
	}
	
	public FocusPanel cloneItem() {
		return new DraggableRegularWorkflowItem(dto, inProgress);
	}

	public void setNextItemsId(int id) {
		dto.setNextId(id);
	}
	
	public int getId() {
		return dto.getId();
	}

	public WorkflowItemDTO getDTO() {
		return dto;
	}
	
	public void setId(int id) {
		dto.setId(id);
	}

	public void refreshDTO(WorkflowItemDTO dto) {
		remove(itemPanel);
		this.dto = dto;
		addItem();
	}
	
}
