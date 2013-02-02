package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardDeletedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardDeletingComponent extends AbstractDeletingComponent implements ModulesLifecycleListener, MessageListener<BoardDto>, Component<BoardWithProjectsDto> {

	private BoardDto boardDto;

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
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, FailableResult<VoidParams>> invokeCommand(
				ServerCommand.DELETE_BOARD,
				new SimpleParams<BoardDto>(boardDto),
				new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(BoardDeletingComponent.this) {

					@Override
					public void success(FailableResult<VoidParams> result) {
						MessageBus.sendMessage(new BoardDeletedMessage(boardDto, this));
					}
				});}});
		
	}

	@Override
	public void messageArrived(Message<BoardDto> message) {
		if (message.getPayload().equals(boardDto)) {
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
	public void setDto(BoardWithProjectsDto dto) {
		this.boardDto = dto.getBoard();
	}
}
