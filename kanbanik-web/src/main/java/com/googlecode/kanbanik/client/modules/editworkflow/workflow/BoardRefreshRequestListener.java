package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardDeletedMessage;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardRefreshRequestListener implements MessageListener<BoardDto> {

	@Override
	public void messageArrived(final Message<BoardDto> message) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager
				.getInvoker()
				.<SimpleParams<BoardDto>, SimpleParams<BoardDto>> invokeCommand(
						ServerCommand.GET_BOARD,
						new SimpleParams<BoardDto>(message.getPayload()),
						new BaseAsyncCallback<SimpleParams<BoardDto>>() {

							@Override
							public void success(SimpleParams<BoardDto> result) {
								// it has been deleted
								if (result.getPayload() == null) {
									MessageBus.sendMessage(new BoardDeletedMessage(message.getPayload(), this));
								} else {
									MessageBus.sendMessage(new BoardChangedMessage(result.getPayload(), this));
								}
							}

						});
		}});		
	}

}
