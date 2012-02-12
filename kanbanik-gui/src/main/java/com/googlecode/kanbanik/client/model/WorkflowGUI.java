package com.googlecode.kanbanik.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.kanbanik.shared.QueuedItemDTO;
import com.googlecode.kanbanik.shared.RegularItemDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;


public class WorkflowGUI {
	
	private List<CompositeWorkflowItemGUI> items = new ArrayList<CompositeWorkflowItemGUI>();
	
	private List<WorkflowItemGUI> workflowItems;
	
	public WorkflowGUI(List<WorkflowItemDTO> workflowItems, Map<WorkflowItemPlaceDTO, WorkflowItemGUI> dtoPlaceToPlace) {
		for (WorkflowItemDTO workflowItem : workflowItems) {
			if (workflowItem instanceof RegularItemDTO) {
				WorkflowItemPlaceDTO placeDTO = ((RegularItemDTO) workflowItem).getPlaces().get(0);
				addItem(new CompositeRegularItemGUI(workflowItem, dtoPlaceToPlace.get(placeDTO)));
			} else if (workflowItem instanceof QueuedItemDTO) {
				if (((QueuedItemDTO) workflowItem).getPlaces().size() != 2) {
					throw new IllegalStateException("It is not possible, that the Queued Item has not exactly two places! Workflow item id = '" + workflowItem.getId() + "'");
				}
				// in gui Im displaying only IN_PROGRESS and DONE
				WorkflowItemPlaceDTO inProgress = ((QueuedItemDTO) workflowItem).getPlaces().get(0);
				WorkflowItemPlaceDTO done = ((QueuedItemDTO) workflowItem).getPlaces().get(1);
				addItem(new QueuedItemGUI(workflowItem, dtoPlaceToPlace.get(inProgress), dtoPlaceToPlace.get(done)));
			}
		}
	}

	public void addItem(CompositeWorkflowItemGUI item) {
		items.add(item);
	}
	
	public List<CompositeWorkflowItemGUI> getItems() {
		return items;
	}

	public List<WorkflowItemGUI> getWorkflowItems() {
		
		if (workflowItems != null) {
			return workflowItems;
		}
		
		List<WorkflowItemGUI> workflowItems = new ArrayList<WorkflowItemGUI>();
		for (CompositeWorkflowItemGUI item : items) {
			workflowItems.addAll(item.getPlaceDescriptors());
		}
		
		this.workflowItems = workflowItems;
		
		return workflowItems;
	}
	
	public WorkflowItemGUI getInputQueue() {
		if (getWorkflowItems() == null || getWorkflowItems().size() == 0) {
			throw new IllegalStateException("No workflow items has been specified so can not return the input queue");
		}
		
		return getWorkflowItems().get(0);
	}
}
