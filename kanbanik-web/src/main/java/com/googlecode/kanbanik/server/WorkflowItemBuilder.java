package com.googlecode.kanbanik.server;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.googlecode.kanbanik.Itemcomposite;
import com.googlecode.kanbanik.Itemleaf;
import com.googlecode.kanbanik.Workflowitem;
import com.googlecode.kanbanik.shared.RegularItemDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;


public class WorkflowItemBuilder {
	
	@PersistenceContext
	private EntityManager manager;
	
	public Workflowitem build(WorkflowDTO workflowDTO, WorkflowItemDTO workflowitemDTO) {
		Workflowitem workflowItem = manager.find(Workflowitem.class, workflowitemDTO.getId());
		
		manager.detach(workflowItem);
		Workflowitem nextItem = manager.find(Workflowitem.class, workflowitemDTO.getNextId());
		manager.detach(nextItem);
		
		if (workflowItem == null) {
			if (workflowitemDTO instanceof RegularItemDTO) {
				workflowItem = new Itemleaf();
			} else {
				workflowItem = new Itemcomposite();
				((Itemcomposite)workflowItem).addLeaf(leafWithName("In progress"));
				((Itemcomposite)workflowItem).addLeaf(leafWithName("Done"));
			}
			
		}
		
		workflowItem.setNextItem(nextItem);
		workflowItem.setName(workflowitemDTO.getName());
		workflowItem.setWipLimit(workflowitemDTO.getWipLimit());
		
		return workflowItem;
	}
	
	private Itemleaf leafWithName(String name) {
		Itemleaf item = new Itemleaf();
		item.setName(name);
		return item;
	}
}
