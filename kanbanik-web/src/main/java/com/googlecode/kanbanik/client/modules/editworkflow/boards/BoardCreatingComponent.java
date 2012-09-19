package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
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
		final BoardDto toStore = new BoardDto();
		toStore.setId(null);
		toStore.setName(dto.getName());
		
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, FailableResult<SimpleParams<BoardDto>>> invokeCommand(
				ServerCommand.SAVE_BOARD,
				new SimpleParams<BoardDto>(toStore),
				new KanbanikAsyncCallback<FailableResult<SimpleParams<BoardDto>>>() {

					@Override
					public void success(FailableResult<SimpleParams<BoardDto>> result) {
						if (result.isSucceeded()) {
							MessageBus.sendMessage(new BoardCreatedMessage( result.getPayload().getPayload(), BoardCreatingComponent.this));	
						} else {
							new ErrorDialog(result.getMessage()).center();
						}
						
					}
				});
		}});
	}
}
