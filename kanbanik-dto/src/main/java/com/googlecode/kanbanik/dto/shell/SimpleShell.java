package com.googlecode.kanbanik.dto.shell;

public class SimpleShell<T> extends BaseShell {
	
	private static final long serialVersionUID = -5531973259119524758L;
	
	private T payload;

	public SimpleShell() {
		// because of GWT
	}
	
	public SimpleShell(T payload) {
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}

}
