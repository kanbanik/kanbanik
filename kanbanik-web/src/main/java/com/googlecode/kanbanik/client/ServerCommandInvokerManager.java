package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ServerCommandInvokerManager {
	private static final ServerCommandInvokerAsync serverCommandInvoker = GWT.create(ServerCommandInvoker.class);
	
	public static final ServerCommandInvokerAsync getInvoker() {
        return new ServerCommandInvokerAsync() {

            public <P extends Params, R extends Result> void invokeCommand(ServerCommand command, P params, AsyncCallback<R> result) {
                String sessionId = CurrentUser.getInstance().getSessionId();
                if (CurrentUser.getInstance().getSessionId() != null) {
                    params.setSessionId(sessionId);
                }

                serverCommandInvoker.invokeCommand(command, params, result);
            }
        };

	}
	
}
