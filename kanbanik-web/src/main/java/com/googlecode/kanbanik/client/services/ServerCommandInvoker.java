package com.googlecode.kanbanik.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.shared.ServerCommand;

@RemoteServiceRelativePath("commandInvoker")
public interface ServerCommandInvoker extends RemoteService {
	
	<P extends Params, R extends Result> R invokeCommand(ServerCommand command, P params);
	
}
