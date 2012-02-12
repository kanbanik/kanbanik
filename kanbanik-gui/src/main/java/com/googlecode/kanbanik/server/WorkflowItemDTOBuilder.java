package com.googlecode.kanbanik.server;

import com.googlecode.kanbanik.Itemcomposite;
import com.googlecode.kanbanik.Itemleaf;
import com.googlecode.kanbanik.Workflowitem;
import com.googlecode.kanbanik.shared.QueuedItemDTO;
import com.googlecode.kanbanik.shared.RegularItemDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;


public class WorkflowItemDTOBuilder {
	
	public WorkflowItemDTO build(Workflowitem item) {
		WorkflowItemDTO dto = null;
		
		if (item instanceof Itemcomposite) {
			dto = new QueuedItemDTO();
			
			for (Itemleaf leaf : ((Itemcomposite)item).getLeafs()) {
				((QueuedItemDTO) dto).addPlace(createPlaceDTO(leaf));
			}
		} else {
			dto = new RegularItemDTO();
			((RegularItemDTO) dto).setPlace(createPlaceDTO(item));
		}
		
		dto.setName(item.getName());
		dto.setId(item.getId());
		dto.setWipLimit(item.getWipLimit());
		if (item.getNextItem() != null) {
			dto.setNextId(item.getNextItem().getId());	
		}
		
		return dto;
	}

	public boolean toAddToDTO(Workflowitem workflowitem) {
		if (workflowitem instanceof Itemleaf) {
			if (((Itemleaf) workflowitem).isSubitem()) {
				// it is considered inside the composite item
				return false;
			}
		}
		
		return true;
	}
	
	private WorkflowItemPlaceDTO createPlaceDTO(Workflowitem item) {
		WorkflowItemPlaceDTO place = new WorkflowItemPlaceDTO();
		place.setId(item.getId());
		return place;
	}
}
