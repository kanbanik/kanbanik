package com.googlecode.kanbanik.dto.shell;

import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class EditWorkflowParams implements Params {
	
	private static final long serialVersionUID = 5036607739533857149L;

	private WorkflowitemDto parent;
	
	private WorkflowitemDto current;
	
	public EditWorkflowParams(WorkflowitemDto parent, WorkflowitemDto current) {
		this.parent = parent;
		this.current = current;
	}

	public EditWorkflowParams() {
	}
	
	public WorkflowitemDto getParent() {
		return parent;
	}

	public void setParent(WorkflowitemDto parent) {
		this.parent = parent;
	}

	public WorkflowitemDto getCurrent() {
		return current;
	}

	public void setCurrent(WorkflowitemDto current) {
		this.current = current;
	}
	
}
