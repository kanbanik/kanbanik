package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardDeletedMessage;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;

public class BoardDeletingComponent extends AbstractDeletingComponent {

	private BoardDTO boardDto;

	private final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);

	public BoardDeletingComponent(HasClickHandlers clickHandler) {
		super(clickHandler);
	}

	public void setBoardDto(BoardDTO boardDto) {
		this.boardDto = boardDto;
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
						configureWorkflowService.deleteBoard(boardDto, new KanbanikAsyncCallback<ReturnObjectDTO>() {

							@Override
							public void success(ReturnObjectDTO result) {
								if (!result.isOK()) {
									new ErrorDialog(result.getMessage()).center();
								} else {
									MessageBus.sendMessage(new BoardDeletedMessage(boardDto, this));
								}
							}

						});				
					}
				}
				);	
	}
}
