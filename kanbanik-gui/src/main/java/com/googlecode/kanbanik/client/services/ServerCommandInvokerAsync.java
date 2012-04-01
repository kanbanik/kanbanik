package com.googlecode.kanbanik.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.kanbanik.dto.shell.SimpleShell;
import com.googlecode.kanbanik.shared.ServerCommand;

public interface ServerCommandInvokerAsync {
	void invokeCommand(ServerCommand command, SimpleShell params, AsyncCallback<SimpleShell> result);
}
