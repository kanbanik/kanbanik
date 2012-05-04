package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;


public class BoardCreatingComponent extends AbstractBoardEditingComponent {

	public BoardCreatingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler, "Add Board");
	}

	@Override
	protected String getBoardName() {
		return "";
	}

	@Override
	protected void onOkClicked(final BoardDto dto) {
		BoardDto toStore = new BoardDto();
		toStore.setId(null);
		toStore.setName(dto.getName());
		
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, SimpleParams<BoardDto>> invokeCommand(
				ServerCommand.SAVE_BOARD,
				new SimpleParams<BoardDto>(toStore),
				new KanbanikAsyncCallback<SimpleParams<BoardDto>>() {

					@Override
					public void success(SimpleParams<BoardDto> result) {
						MessageBus.sendMessage(new BoardCreatedMessage( result.getPayload(), BoardCreatingComponent.this));
					}
				});
	}
}
