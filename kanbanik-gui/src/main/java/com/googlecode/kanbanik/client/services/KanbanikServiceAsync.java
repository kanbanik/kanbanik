package com.googlecode.kanbanik.client.services;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.shared.KanbanikDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public interface KanbanikServiceAsync {

	void loadKanbanikData(AsyncCallback<KanbanikDTO> callback);

	void saveTask(TaskDTO task, AsyncCallback<TaskDTO> callback);
	
	void deleteTask(TaskDTO task, AsyncCallback<Void> callback);

}
