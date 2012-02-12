package com.googlecode.kanbanik.shared;

import java.io.Serializable;
import java.util.List;

public abstract class WorkflowItemDTO implements Serializable {

	private static final long serialVersionUID = -2389680680081116931L;

	private String name;
	
	private int id;
	
	private int wipLimit;
	
	private int nextId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNextId() {
		return nextId;
	}

	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
	
	public int getWipLimit() {
		return wipLimit;
	}
	
	public boolean hasWipLimit() {
		return wipLimit != -1;
	}

	public void setWipLimit(int wipLimit) {
		this.wipLimit = wipLimit;
	}

	public abstract List<WorkflowItemPlaceDTO> getPlaces();
}
