package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardDeletedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

public class BoardDeletingComponent extends AbstractDeletingComponent implements ModulesLifecycleListener, MessageListener<Dtos.BoardDto>, Component<Dtos.BoardWithProjectsDto> {

	private Dtos.BoardDto boardDto;

	public BoardDeletingComponent(HasClickHandlers clickHandler) {
		super(clickHandler);
		
		registerListeners();
	}

	public BoardDeletingComponent() {
		registerListeners();
	}
	
	private void registerListeners() {
		MessageBus.registerListener(BoardChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
	
	@Override
	protected String getMessageSpecificPart() {
		return "board with name: '" + boardDto.getName() + "'";
	}

	@Override
	protected void onOkClicked() {

        boardDto.setSessionId(CurrentUser.getInstance().getSessionId());
        boardDto.setCommandName(CommandNames.DELETE_BOARD.name);

        ServerCaller.<Dtos.BoardDto, Dtos.EmptyDto>sendRequest(
                boardDto,
                Dtos.EmptyDto.class,
                new ResourceClosingCallback<Dtos.EmptyDto>(BoardDeletingComponent.this) {

                    @Override
                    public void success(Dtos.EmptyDto response) {
                        MessageBus.sendMessage(new BoardDeletedMessage(boardDto, this));
                    }
                }
        );
	}

	@Override
	public void messageArrived(Message<Dtos.BoardDto> message) {
		if (message.getPayload().getId().equals(boardDto.getId())) {
			boardDto = message.getPayload();
		}
	}

	@Override
	public void activated() {
		if (!MessageBus.listens(BoardChangedMessage.class, this)) {
			MessageBus.registerListener(BoardChangedMessage.class, this);	
		}
	}

	@Override
	public void deactivated() {
		MessageBus.unregisterListener(BoardChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(Dtos.BoardWithProjectsDto dto) {
		this.boardDto = dto.getBoard();
	}
}
