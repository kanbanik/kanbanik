package com.googlecode.kanbanik.client.services;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.kanbanik.shared.KanbanikDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

@RemoteServiceRelativePath("kanbanik")
public interface KanbanikService extends RemoteService {
	
	KanbanikDTO loadKanbanikData();
	
	TaskDTO saveTask(TaskDTO task);
	
	void deleteTask(TaskDTO task);
}
