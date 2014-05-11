package com.googlecode.kanbanik.client;

import com.googlecode.kanbanik.client.modules.BoardsModule;
import com.googlecode.kanbanik.client.modules.ConfigureWorkflowModule;
import com.googlecode.kanbanik.client.modules.ControlPanelModule;
import com.googlecode.kanbanik.client.modules.SecurityModule;

public enum Modules {
	
	BOARDS(BoardsModule.class), 
	CONTROL(ControlPanelModule.class),
    SECURITY_MODULE(SecurityModule.class),
	CONFIGURE(ConfigureWorkflowModule.class);


	private Class<?> clazz;
	
	private Modules(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> toClass() {
		return clazz;
	}
}
