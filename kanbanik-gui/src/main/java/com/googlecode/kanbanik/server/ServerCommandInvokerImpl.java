package com.googlecode.kanbanik.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.commands.NewProjectServerCommand;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleShell;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ServerCommandInvokerImpl extends RemoteServiceServlet implements ServerCommandInvoker {

	private static final long serialVersionUID = 5624445329544670227L;

	@SuppressWarnings("unchecked")
	public SimpleShell invokeCommand(ServerCommand command, SimpleShell params) {
		if (command == ServerCommand.NEW_PROJECT) {
			return new NewProjectServerCommand().execute((SimpleShell<ProjectDto>) params);
		}
		return null;
	}

}
