package com.googlecode.kanbanik.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.kanbanik.dto.shell.SimpleShell;
import com.googlecode.kanbanik.shared.ServerCommand;

@RemoteServiceRelativePath("commandInvoker")
public interface ServerCommandInvoker extends RemoteService {
	
	SimpleShell invokeCommand(ServerCommand command, SimpleShell params);
	
}
