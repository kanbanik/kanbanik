package com.googlecode.kanbanik.server;

import org.apache.shiro.SecurityUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.commands.AddProjectsToBoardCommand;
import com.googlecode.kanbanik.commands.DeleteBoardCommand;
import com.googlecode.kanbanik.commands.DeleteProjectCommand;
import com.googlecode.kanbanik.commands.DeleteTaskCommand;
import com.googlecode.kanbanik.commands.DeleteWorkflowitemCommand;
import com.googlecode.kanbanik.commands.EditWorkflowCommand;
import com.googlecode.kanbanik.commands.EditWorkflowitemDataCommand;
import com.googlecode.kanbanik.commands.GetAllBoardsCommand;
import com.googlecode.kanbanik.commands.GetAllProjectsCommand;
import com.googlecode.kanbanik.commands.LoginCommand;
import com.googlecode.kanbanik.commands.GetBoardCommand;
import com.googlecode.kanbanik.commands.MoveTaskCommand;
import com.googlecode.kanbanik.commands.RemoveProjectFromBoardCommand;
import com.googlecode.kanbanik.commands.SaveBoardCommand;
import com.googlecode.kanbanik.commands.SaveProjectCommand;
import com.googlecode.kanbanik.commands.LogoutCommand;
import com.googlecode.kanbanik.commands.SaveTaskCommand;
import com.googlecode.kanbanik.commands.GetCurrentUserCommand;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ServerCommandInvokerImpl extends RemoteServiceServlet implements ServerCommandInvoker {

	private static final long serialVersionUID = 5624445329544670227L;

	// well... Anybody an idea how to get rid of this huge if-else statement?
	@SuppressWarnings("unchecked")
	public <P extends Params, R extends Result> R invokeCommand(ServerCommand command, P params) {
		
		boolean isLoggedIn = SecurityUtils.getSubject().isAuthenticated();
		
		// unsecure zone - anyone can call this commands whatever the user is logged in or not
		if (command == ServerCommand.LOGIN_COMMAND) {
			return (R) new LoginCommand().execute(params);
		} else if (command == ServerCommand.GET_CURRENT_USER_COMMAND) {
			return (R) new GetCurrentUserCommand().execute(params);
		}
		
		
		if (!isLoggedIn) {
			throw new SecurityException("Not logged in user is not can not access any command except the login command");
		}
		
		// secure zone - unly logged in users can call this commands
		
		if (command == ServerCommand.LOGOUT_COMMAND) {
			return (R) new LogoutCommand().execute(params);
		}else if (command == ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS) {
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
		} else if (command == ServerCommand.SAVE_PROJECT) {
			return (R) new SaveProjectCommand().execute(params);
		} else if (command == ServerCommand.DELETE_PROJECT) {
			return (R) new DeleteProjectCommand().execute(params);
		} else if (command == ServerCommand.ADD_PROJECTS_TO_BOARD) {
			return (R) new AddProjectsToBoardCommand().execute(params);
		} else if (command == ServerCommand.REMOVE_PROJECTS_FROM_BOARD) {
			return (R) new RemoveProjectFromBoardCommand().execute(params);
		} else if (command == ServerCommand.EDIT_WORKFLOW) {
			return (R) new EditWorkflowCommand().execute(params);
		} else if (command == ServerCommand.GET_BOARD) {
			return (R) new GetBoardCommand().execute(params);
		} else if (command == ServerCommand.DELETE_WORKFLOWITEM) {
			return (R) new DeleteWorkflowitemCommand().execute(params);
		} else if (command == ServerCommand.EDIT_WORKFLOWITEM_DATA) {
			return (R) new EditWorkflowitemDataCommand().execute(params);
		}
		return null;
	}

}
