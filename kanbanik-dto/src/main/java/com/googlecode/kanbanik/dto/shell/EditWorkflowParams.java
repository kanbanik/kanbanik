package com.googlecode.kanbanik.dto.shell;

import com.googlecode.kanbanik.dto.WorkflowDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class EditWorkflowParams implements Params {
	
	private static final long serialVersionUID = 5036607739533857149L;

	private WorkflowitemDto current;
	
	private WorkflowitemDto next;
	
	private WorkflowDto destContext;

    private String sessionId;

    public EditWorkflowParams(WorkflowitemDto current, WorkflowitemDto next,
			WorkflowDto destContext) {
		super();
		this.current = current;
		this.next = next;
		this.destContext = destContext;
	}

	public EditWorkflowParams() {
	}

	public WorkflowitemDto getCurrent() {
		return current;
	}

	public void setCurrent(WorkflowitemDto current) {
		this.current = current;
	}

	public WorkflowitemDto getNext() {
		return next;
	}

	public void setNext(WorkflowitemDto next) {
		this.next = next;
	}

	public WorkflowDto getDestContext() {
		return destContext;
	}

	public void setDestContext(WorkflowDto destContext) {
		this.destContext = destContext;
	}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
