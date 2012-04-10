package com.googlecode.kanbanik;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Workflow implements Serializable {

	private static final long serialVersionUID = 2823591685830420160L;

	@Id
	@GeneratedValue
	private int id;

	@OneToMany
	private Collection<Workflowitem> workflowitems;

	public Workflowitems getWorkflowitems() {
		return new Workflowitems(this);
	}

	public int getId() {
		return id;
	}

	Collection<Workflowitem> itemsAsList() {
		return workflowitems;
	}

	public void insertItem(EntityManager manager, Workflowitem workflowItem) {
		Workflow thisWorkflow = manager.find(Workflow.class, id);
		thisWorkflow.workflowitems.add(manager.find(Workflowitem.class, workflowItem.getId()));
	}

	void remove(EntityManager manager, Workflowitem workflowItem) {
		Workflow thisWorkflow = manager.find(Workflow.class, id);
		thisWorkflow.workflowitems.remove(manager.find(Workflowitem.class, workflowItem.getId()));
	}

	public boolean containsItem(Workflowitem item) {
		if (workflowitems.contains(item)) {
			return true;
		}

		return false;
	}
}
