package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.model.QueuedItemGUI;
import com.googlecode.kanbanik.client.model.WorkflowItemGUI;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class DraggableQueuedItem extends FocusPanel implements DraggableWorkflowItem {

	private WorkflowItemDTO dto;
	
	private WorkflowItemGUI inProgress;
	
	private WorkflowItemGUI done;

	private Widget itemPanel;
	
	public DraggableQueuedItem(WorkflowItemDTO dto, WorkflowItemGUI inProgress, WorkflowItemGUI done) {
		setStyleName("draggable-editable-workflow-item");
		this.dto = dto;
		this.inProgress = inProgress;
		this.done = done;
		addEditableItem();
		setTitle(dto.getName());
	}

	private void addEditableItem() {
		itemPanel = new QueuedItemGUI(dto, inProgress, done);
		add(itemPanel);
	}
	
	public FocusPanel cloneItem() {
		return new DraggableQueuedItem(dto, inProgress, done);
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
		addEditableItem();
	}

}
