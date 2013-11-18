package com.googlecode.kanbanik.dto.shell;

public class VoidParams implements Params, Result {

	private static final long serialVersionUID = 7676547376785884800L;

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
