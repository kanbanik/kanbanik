package com.googlecode.kanbanik.client.model;

import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;

public class WorkflowItemGUI {

	private String cssName;

	private WorkflowItemPlaceDTO dto;
	
	public WorkflowItemGUI(WorkflowItemPlaceDTO dto) {
		this.dto = dto;
	}

	public String getCssName() {
		return cssName;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	public WorkflowItemPlaceDTO getDto() {
		return dto;
	}
	
}
