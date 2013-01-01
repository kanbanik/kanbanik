package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardEditingComponent extends AbstractBoardEditingComponent implements ModulesLifecycleListener, MessageListener<BoardDto>  {
	
	public BoardEditingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler, "Edit Board");
		
		MessageBus.registerListener(BoardChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	private BoardDto boardDto;

	@Override
	protected String getBoardName() {
		if (boardDto == null) {
			return "";
		}
		return boardDto.getName();
	}

	@Override
	protected void onOkClicked(BoardDto dto) {
		final BoardDto toStore = new BoardDto();
		toStore.setId(boardDto.getId());
		toStore.setName(dto.getName());
		toStore.setVersion(boardDto.getVersion());
		toStore.setWorkflow(boardDto.getWorkflow());
		
		new KanbanikServerCaller(
				new Runnable() {
					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, FailableResult<SimpleParams<BoardDto>>> invokeCommand(
				ServerCommand.SAVE_BOARD,
				new SimpleParams<BoardDto>(toStore),
				new ResourceClosingAsyncCallback<FailableResult<SimpleParams<BoardDto>>>(BoardEditingComponent.this) {

					@Override
					public void success(FailableResult<SimpleParams<BoardDto>> result) {
						MessageBus.sendMessage(new BoardEditedMessage(result.getPayload().getPayload(), this));
						MessageBus.sendMessage(new BoardChangedMessage(result.getPayload().getPayload(), this));
					}
				});
		}});
		
	}

	public void setBoardDto(BoardDto boardDto) {
		this.boardDto = boardDto;
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

}
