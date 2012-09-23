package com.googlecode.kanbanik.client;

import com.googlecode.kanbanik.client.components.Closable;

public class ResourceClosingAsyncCallback<T> extends BaseAsyncCallback<T> {
	
	private Closable closable;

	public ResourceClosingAsyncCallback(Closable closable) {
		super();
		this.closable = closable;
	}

	public void beforeSuccess(T result) {
		if (closable != null) {
			closable.close();
		}
	}

}
