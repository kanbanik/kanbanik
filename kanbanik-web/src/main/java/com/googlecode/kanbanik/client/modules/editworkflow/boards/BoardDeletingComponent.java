package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.messages.BoardDeletedMessage;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class BoardDeletingComponent extends AbstractDeletingComponent {

	private BoardDto boardDto;


	public BoardDeletingComponent(HasClickHandlers clickHandler) {
		super(clickHandler);
	}

	public void setBoardDto(BoardDto boardDto) {
		this.boardDto = boardDto;
	}

	@Override
	protected String getMessageSpecificPart() {
		return "board with name: '" + boardDto.getName() + "'";
	}

	@Override
	protected void onOkClicked() {

		final BoardDto toDelete = new BoardDto();
		toDelete.setId(boardDto.getId());
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, FailableResult<VoidParams>> invokeCommand(
				ServerCommand.DELETE_BOARD,
				new SimpleParams<BoardDto>(toDelete),
				new ResourceClosingAsyncCallback<FailableResult<VoidParams>>(BoardDeletingComponent.this) {

					@Override
					public void success(FailableResult<VoidParams> result) {
						MessageBus.sendMessage(new BoardDeletedMessage(boardDto, this));
					}
				});}});
		
	}
}
