package com.googlecode.kanbanik.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.commands.DeleteTaskCommand;
import com.googlecode.kanbanik.commands.GetAllBoardsCommand;
import com.googlecode.kanbanik.commands.MoveTaskCommand;
import com.googlecode.kanbanik.commands.SaveTaskCommand;
import com.googlecode.kanbanik.commands.GetAllProjectsCommand;
import com.googlecode.kanbanik.commands.SaveBoardCommand;
import com.googlecode.kanbanik.commands.DeleteBoardCommand;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ServerCommandInvokerImpl extends RemoteServiceServlet implements ServerCommandInvoker {

	private static final long serialVersionUID = 5624445329544670227L;

	@SuppressWarnings("unchecked")
	public <P extends Params, R extends Result> R invokeCommand(ServerCommand command, P params) {
		if (command == ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS) {
			return (R) new GetAllBoardsCommand().execute(params);
		} else if (command == ServerCommand.MOVE_TASK) {
			return (R) new MoveTaskCommand().execute(params);
		} else if (command == ServerCommand.SAVE_TASK) {
			return (R) new SaveTaskCommand().execute(params); 
		} else if (command == ServerCommand.DELETE_TASK) {
			return (R) new DeleteTaskCommand().execute(params);
		} else if (command == ServerCommand.GET_ALL_PROJECTS) {
			return (R) new GetAllProjectsCommand().execute(params);
		} else if (command == ServerCommand.SAVE_BOARD) {
			return (R) new SaveBoardCommand().execute(params);
		} else if (command == ServerCommand.DELETE_BOARD) {
			return (R) new DeleteBoardCommand().execute(params);
		}
		return null;
	}

}
