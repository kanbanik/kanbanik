package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardEditedMessage;
import com.googlecode.kanbanik.shared.BoardDTO;

public class BoardEditingComponent extends AbstractBoardEditingComponent {
	
	public BoardEditingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler);
	}

	private BoardDTO boardDto;

	@Override
	protected String getBoardName() {
		if (boardDto == null) {
			return "";
		}
		return boardDto.getName();
	}

	@Override
	protected void onOkClicked(BoardDTO dto) {
		final BoardDTO toStore = new BoardDTO();
		toStore.setId(boardDto.getId());
		toStore.setName(dto.getName());
		
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				configureWorkflowService.editBoard(toStore,
						new KanbanikAsyncCallback<Void>() {

							@Override
							public void success(Void result) {
								MessageBus.sendMessage(new BoardEditedMessage(toStore, this));
							}
						}
				);
			}
		});
	}

	public void setBoardDto(BoardDTO boardDto) {
		this.boardDto = boardDto;
	}

}
