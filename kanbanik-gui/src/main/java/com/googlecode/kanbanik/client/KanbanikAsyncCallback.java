package com.googlecode.kanbanik.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.client.components.ErrorDialog;

public abstract class KanbanikAsyncCallback<T> implements AsyncCallback<T> {

	public void onFailure(Throwable caught) {
		KanbanikProgressBar.hide();
		new ErrorDialog(caught).show();
	}
	
	public void onSuccess(T result) {
		KanbanikProgressBar.hide();
		success(result);
	}

	public abstract void success(T result);
}
