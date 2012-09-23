package com.googlecode.kanbanik.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.dto.shell.FailableResult;

public class BaseAsyncCallback<T> implements AsyncCallback<T> {

	public void onFailure(Throwable caught) {
		KanbanikProgressBar.hide();
		new ErrorDialog(caught).show();
	}
	
	public void onSuccess(T result) {
		KanbanikProgressBar.hide();
		
		if (result instanceof FailableResult<?>) {
			FailableResult<?> failableResult = (FailableResult<?>) result;
			if (!failableResult.isSucceeded()) {
				new ErrorDialog(failableResult.getMessage()).center();
				failure(result);
			} else {
				beforeSuccess(result);
				success(result);
			}
		} else {
			beforeSuccess(result);
			success(result);
		}
		
	}

	/**
	 * This method is not meant to be owerridden in client code
	 */
	void beforeSuccess(T result) {
		
	}
	
	public void success(T result) {
	}
	
	public void failure(T result) {
	}
}
