package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class BoardDto implements KanbanikDto {

	private static final long serialVersionUID = -4409696591604175858L;

	private String name;
	
	private String id;

	private List<WorkflowitemDto> workflowitems = new ArrayList<WorkflowitemDto>();

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WorkflowitemDto> getWorkflowitems() {
		return workflowitems;
	}

	public void addWorkflowitem(WorkflowitemDto workflowitem) {
		workflowitems.add(workflowitem);
	}
}
