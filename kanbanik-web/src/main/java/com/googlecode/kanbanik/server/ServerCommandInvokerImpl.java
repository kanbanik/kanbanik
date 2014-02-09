package com.googlecode.kanbanik.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.commands.AddProjectsToBoardCommand;
import com.googlecode.kanbanik.commands.DeleteBoardCommand;
import com.googlecode.kanbanik.commands.DeleteTasksCommand;
import com.googlecode.kanbanik.commands.DeleteWorkflowitemCommand;
import com.googlecode.kanbanik.commands.EditWorkflowCommand;
import com.googlecode.kanbanik.commands.EditWorkflowitemDataCommand;
import com.googlecode.kanbanik.commands.GetAllBoardsCommand;
import com.googlecode.kanbanik.commands.GetBoardCommand;
import com.googlecode.kanbanik.commands.GetTaskCommand;
import com.googlecode.kanbanik.commands.MoveTaskCommand;
import com.googlecode.kanbanik.commands.RemoveProjectFromBoardCommand;
import com.googlecode.kanbanik.commands.SaveBoardCommand;
import com.googlecode.kanbanik.commands.SaveTaskCommand;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.GetAllBoardsWithProjectsParams;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams;
import com.googlecode.kanbanik.dto.shell.MoveTaskParams;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;
import com.googlecode.kanbanik.shared.UserNotLoggedInException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class ServerCommandInvokerImpl extends RemoteServiceServlet implements ServerCommandInvoker {

	private static final long serialVersionUID = 5624445329544670227L;

	// well... Anybody an idea how to get rid of this huge if-else statement?
    // nvm, rewriting to scala anyway :)
	@SuppressWarnings("unchecked")
	public <P extends Params, R extends Result> R invokeCommand(ServerCommand command, P params) throws UserNotLoggedInException {
        boolean isLoggedIn = false;

        Subject subject = SecurityUtils.getSubject();

        String sessionId = params.getSessionId();
        if (sessionId != null && !"".equals(sessionId)) {
            subject = new Subject.Builder().sessionId(sessionId).buildSubject();
        }

        isLoggedIn = subject.isAuthenticated();

		// unsecure zone - anyone can call this commands whatever the user is logged in or not

		if (!isLoggedIn) {
			throw new UserNotLoggedInException();
		}
		
		// secure zone - only logged in users can call this commands
		if (command == ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS) {
			return (R) new GetAllBoardsCommand().execute((GetAllBoardsWithProjectsParams) params);
		} else if (command == ServerCommand.SAVE_BOARD) {
			return (R) new SaveBoardCommand().execute((SimpleParams<BoardDto>) params);
		} else if (command == ServerCommand.DELETE_BOARD) {
			return (R) new DeleteBoardCommand().execute((SimpleParams<BoardDto>) params);
		} else if (command == ServerCommand.EDIT_WORKFLOW) {
			return (R) new EditWorkflowCommand().execute((EditWorkflowParams) params);
		} else if (command == ServerCommand.GET_BOARD) {
			return (R) new GetBoardCommand().execute((SimpleParams<BoardDto>) params);
		} else if (command == ServerCommand.DELETE_WORKFLOWITEM) {
			return (R) new DeleteWorkflowitemCommand().execute((SimpleParams<WorkflowitemDto>) params);
		} else if (command == ServerCommand.EDIT_WORKFLOWITEM_DATA) {
			return (R) new EditWorkflowitemDataCommand().execute((SimpleParams<WorkflowitemDto>) params);
        }

		return null;
	}

}
