package com.googlecode.kanbanik.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.dto.shell.Params;
import com.googlecode.kanbanik.dto.shell.Result;
import com.googlecode.kanbanik.shared.ServerCommand;

public interface ServerCommandInvokerAsync {
	<P extends Params, R extends Result> void invokeCommand(ServerCommand command, P params, AsyncCallback<R> result);
}
