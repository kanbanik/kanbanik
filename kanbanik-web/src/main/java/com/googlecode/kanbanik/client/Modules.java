package com.googlecode.kanbanik.client;

import com.googlecode.kanbanik.client.modules.BoardsModuleOld;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.modules.editworkflow.ConfigureWorkflowModule;

public enum Modules {
	
	BOARDS(BoardsModuleOld.class), 
	CONTROL(ControlPanelModule.class), 
	CONFIGURE(ConfigureWorkflowModule.class);

	private Class<?> clazz;
	
	private Modules(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> toClass() {
		return clazz;
	}
}
