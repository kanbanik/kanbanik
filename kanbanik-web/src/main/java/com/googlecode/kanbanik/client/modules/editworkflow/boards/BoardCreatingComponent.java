package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardCreatedMessage;
import com.googlecode.kanbanik.client.modules.SecurityModule;
import com.googlecode.kanbanik.dto.CommandNames;


public class BoardCreatingComponent extends AbstractBoardEditingComponent {

	public BoardCreatingComponent(HasClickHandlers hasClickHandler) {
		super(hasClickHandler, "Add Board");
	}
	
	public BoardCreatingComponent() {
		
	}

	@Override
	protected String getBoardName() {
		return "";
	}
	
	@Override
	protected Dtos.WorkflowVerticalSizing getVerticalSizing() {
		return Dtos.WorkflowVerticalSizing.MIN_POSSIBLE;
	}

	@Override
	protected boolean isUserPictureDisplayingEnabled() {
		return true;
	}

    @Override
    protected boolean fixedSizeShortDescriptionEnabled() {
        return false;
    }

    @Override
	protected void onOkClicked(final Dtos.BoardDto dto) {
		final Dtos.BoardDto toStore = DtoFactory.boardDto();
		toStore.setId(null);
		toStore.setName(dto.getName());
		toStore.setWorkflowVerticalSizing(dto.getWorkflowVerticalSizing());
		toStore.setShowUserPictureEnabled(dto.isShowUserPictureEnabled());
        toStore.setFixedSizeShortDescription(dto.isFixedSizeShortDescription());
		toStore.setWorkflow(DtoFactory.workflowDto());
        toStore.setCommandName(CommandNames.CREATE_BOARD.name);
        toStore.setVersion(1);

        ServerCaller.<Dtos.BoardDto, Dtos.BoardDto>sendRequest(
                toStore,
                Dtos.BoardDto.class,
                new ResourceClosingCallback<Dtos.BoardDto>(BoardCreatingComponent.this) {

                    @Override
                    public void success(Dtos.BoardDto response) {
                        MessageBus.sendMessage(new BoardCreatedMessage(response, BoardCreatingComponent.this));
                        MessageBus.sendMessage(new BoardChangedMessage(response, BoardCreatingComponent.this));
                        UsersManager.getInstance().updateCurrentUser();
                    }
                }
        );

	}

}
