package com.googlecode.kanbanik.dto.shell;

public class MidAirCollisionResult<T extends Result> extends FailableResult<T> {

	private static final long serialVersionUID = -8087029239537656391L;

	public MidAirCollisionResult() {
		super();
	}

	public MidAirCollisionResult(T payload, boolean succeeded, String message) {
		super(payload, succeeded, message);
	}

	public MidAirCollisionResult(T payload) {
		super(payload);
	}
	
}
