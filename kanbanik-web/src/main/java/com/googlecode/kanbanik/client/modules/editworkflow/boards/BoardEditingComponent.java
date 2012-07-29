package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.BoardEditedMessage;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardEditingComponent extends AbstractBoardEditingComponent {
	
	public BoardEditingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler, "Edit Board");
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
		
		new KanbanikServerCaller(
				new Runnable() {
					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, SimpleParams<BoardDto>> invokeCommand(
				ServerCommand.SAVE_BOARD,
				new SimpleParams<BoardDto>(toStore),
				new KanbanikAsyncCallback<SimpleParams<BoardDto>>() {

					@Override
					public void success(SimpleParams<BoardDto> result) {
						MessageBus.sendMessage(new BoardEditedMessage(result.getPayload(), this));
					}
				});
		}});
		
	}

	public void setBoardDto(BoardDto boardDto) {
		this.boardDto = boardDto;
	}

}
