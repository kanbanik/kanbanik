package com.googlecode.kanbanik.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkflowDTO implements Serializable {

	private static final long serialVersionUID = -5311514189658984809L;
	
	private List<WorkflowItemDTO> workflowItems = new ArrayList<WorkflowItemDTO>();
	
	private int id;

	public List<WorkflowItemDTO> getWorkflowItems() {
		return workflowItems;
	}

	public void addWorkflowItem(WorkflowItemDTO workflowItemDTO) {
		workflowItems.add(workflowItemDTO);
	}

	public WorkflowItemPlaceDTO placeById(int id) {
		// TODO make it more efficient!
		for (WorkflowItemDTO workflowItem : workflowItems) {
			for (WorkflowItemPlaceDTO place : workflowItem.getPlaces()) {
				if (place.getId() == id) {
					return place;
				}
			}
		}
		
		return null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
