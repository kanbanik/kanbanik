package com.googlecode.kanbanik.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.commands.NewProjectServerCommand;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ServerCommandInvokerImpl extends RemoteServiceServlet implements ServerCommandInvoker {

	private static final long serialVersionUID = 5624445329544670227L;

	@SuppressWarnings("unchecked")
	public <P extends Params, R extends Result> R invokeCommand(ServerCommand command, P params) {
		if (command == ServerCommand.NEW_PROJECT) {
			return (R) new NewProjectServerCommand().execute(params);
		}
		return null;
	}

}
