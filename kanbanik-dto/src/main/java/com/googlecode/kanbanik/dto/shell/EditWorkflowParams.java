package com.googlecode.kanbanik.dto.shell;

import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class EditWorkflowParams implements Params {
	
	private static final long serialVersionUID = 5036607739533857149L;

	private WorkflowitemDto parent;
	
	private WorkflowitemDto current;
	
	private WorkflowitemDto context;
	
	public EditWorkflowParams(WorkflowitemDto parent, WorkflowitemDto current, WorkflowitemDto context) {
		this.parent = parent;
		this.current = current;
		this.context = context;
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

	public WorkflowitemDto getContext() {
		return context;
	}

	public void setContext(WorkflowitemDto context) {
		this.context = context;
	}
	
}
