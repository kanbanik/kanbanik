package com.googlecode.kanbanik.dto.shell;

public class FailableResult<T extends Result> implements Result {
	
	private static final long serialVersionUID = -2524662458235989743L;

	private T payload;
	
	private boolean succeeded;
	
	private String message;

	public FailableResult() {
		// because of GWT
	}
	
	public FailableResult(T payload, boolean succeeded, String message) {
		super();
		this.payload = payload;
		this.succeeded = succeeded;
		this.message = message;
	}

	public T getPayload() {
		return payload;
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public String getMessage() {
		return message;
	}
	
	
}
