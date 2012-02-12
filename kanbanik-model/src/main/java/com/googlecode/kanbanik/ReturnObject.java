package com.googlecode.kanbanik;

public class ReturnObject {
	
	private boolean isOK;
	
	private String message;
	
	public ReturnObject(boolean isOK, String message) {
		super();
		this.isOK = isOK;
		this.message = message;
	}

	public boolean isOK() {
		return isOK;
	}

	public String getMessage() {
		return message;
	}
}
