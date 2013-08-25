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
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardEditingComponent extends AbstractBoardEditingComponent implements ModulesLifecycleListener, MessageListener<BoardDto>  {
	
	public BoardEditingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler, "Edit Board");
		
		registerListeners();
	}

	private void registerListeners() {
		MessageBus.registerListener(BoardChangedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
	
	public BoardEditingComponent() {
		registerListeners();
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
	protected WorkfloVerticalSizing getVerticalSizing() {
		if (boardDto == null) {
			return WorkfloVerticalSizing.BALANCED;
		}
		
		return boardDto.getWorkfloVerticalSizing();
	}
	
	@Override
	protected boolean isUserPictureDisplayingEnabled() {
		if (boardDto == null) {
			return true;
		}
		
		return boardDto.isShowUserPictureEnabled();
	}

	@Override
	protected void onOkClicked(BoardDto dto) {
		final BoardDto toStore = new BoardDto();
		toStore.setId(boardDto.getId());
		toStore.setName(dto.getName());
		toStore.setWorkfloVerticalSizing(dto.getWorkfloVerticalSizing());
		toStore.setVersion(boardDto.getVersion());
		toStore.setShowUserPictureEnabled(dto.isShowUserPictureEnabled());
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

	@Override
	public void setDto(BoardWithProjectsDto dto) {
		this.boardDto = dto.getBoard();
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
