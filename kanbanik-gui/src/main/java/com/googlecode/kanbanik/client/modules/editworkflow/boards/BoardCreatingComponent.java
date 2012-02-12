package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.shared.BoardDTO;


public class BoardCreatingComponent extends AbstractBoardEditingComponent {

	public BoardCreatingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler);
	}

	@Override
	protected String getBoardName() {
		return "";
	}

	@Override
	protected void onOkClicked(final BoardDTO dto) {
		dto.setId(-1);

		new KanbanikServerCaller(new Runnable() {

			public void run() {
				configureWorkflowService.createNewBoard(dto,
						new KanbanikAsyncCallback<BoardDTO>() {
							@Override
							public void success(BoardDTO result) {
								MessageBus.sendMessage(new BoardCreatedMessage( result, BoardCreatingComponent.this));
							}
						}
				);
			}
		});
	}
}
