package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardDeletedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class BoardRefreshRequestListener implements MessageListener<Dtos.BoardDto> {

	@Override
	public void messageArrived(final Message<Dtos.BoardDto> message) {
        message.getPayload().setSessionId(CurrentUser.getInstance().getSessionId());
        message.getPayload().setCommandName(CommandNames.GET_BOARD.name);

        ServerCaller.<Dtos.BoardDto, Dtos.BoardDto>sendRequest(
                message.getPayload(),
                Dtos.BoardDto.class,
                new ServerCallCallback<Dtos.BoardDto>() {

                    @Override
                    public void success(Dtos.BoardDto response) {
                        // it has been deleted
                        if (response.getId() == null) {
                            MessageBus.sendMessage(new BoardDeletedMessage(message.getPayload(), this));
                        } else {
                            MessageBus.sendMessage(new BoardChangedMessage(response, this));
                        }
                    }

                }
        );
    }

}
