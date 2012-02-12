package com.googlecode.kanbanik.shared;

import java.io.Serializable;

public class ReturnObjectDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private boolean isOK;
	
	private String message;
	
	public ReturnObjectDTO(boolean isOK, String message) {
		super();
		this.isOK = isOK;
		this.message = message;
	}
	
	public ReturnObjectDTO() {
		super();
	}

	public boolean isOK() {
		return isOK;
	}

	public String getMessage() {
		return message;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
