package com.googlecode.kanbanik.shared;

import java.io.Serializable;

public class WorkflowItemPlaceDTO implements Serializable {

	private static final long serialVersionUID = 7722571451366176155L;
	
	private int id;
	
	public WorkflowItemPlaceDTO(int id) {
		super();
		this.id = id;
	}
	
	public WorkflowItemPlaceDTO() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
