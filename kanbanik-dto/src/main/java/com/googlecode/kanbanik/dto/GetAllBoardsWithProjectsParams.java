package com.googlecode.kanbanik.dto;

import com.googlecode.kanbanik.dto.shell.Params;

public class GetAllBoardsWithProjectsParams implements Params {

	private static final long serialVersionUID = 122018133897580619L;

	private boolean includeTasks;

    private String sessionId;

    public GetAllBoardsWithProjectsParams() {
		// because of GWT serialization
	}
	
	public GetAllBoardsWithProjectsParams(boolean includeTasks) {
		super();
		this.includeTasks = includeTasks;
	}

	public boolean isIncludeTasks() {
		return includeTasks;
	}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
