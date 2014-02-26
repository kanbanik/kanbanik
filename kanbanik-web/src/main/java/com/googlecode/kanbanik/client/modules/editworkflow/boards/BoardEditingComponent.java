package com.googlecode.kanbanik.client.modules.editworkflow.boards;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.CommandNames;

public class BoardEditingComponent extends AbstractBoardEditingComponent implements ModulesLifecycleListener, MessageListener<Dtos.BoardDto>  {
	
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

	private Dtos.BoardDto boardDto;

	@Override
	protected String getBoardName() {
		if (boardDto == null) {
			return "";
		}
		return boardDto.getName();
	}
	
	@Override
	protected Dtos.WorkflowVerticalSizing getVerticalSizing() {
		if (boardDto == null) {
			return Dtos.WorkflowVerticalSizing.BALANCED;
		}
		
		return Dtos.WorkflowVerticalSizing.from(boardDto.getWorkflowVerticalSizing());
	}
	
	@Override
	protected boolean isUserPictureDisplayingEnabled() {
		if (boardDto == null) {
			return true;
		}
		
		return boardDto.isShowUserPictureEnabled();
	}

	@Override
	protected void onOkClicked(Dtos.BoardDto dto) {
		final Dtos.BoardDto toStore = DtoFactory.boardDto();
		toStore.setId(boardDto.getId());
		toStore.setName(dto.getName());
		toStore.setWorkflowVerticalSizing(dto.getWorkflowVerticalSizing());
		toStore.setVersion(boardDto.getVersion());
		toStore.setShowUserPictureEnabled(dto.isShowUserPictureEnabled());
		toStore.setWorkflow(boardDto.getWorkflow());
        toStore.setCommandName(CommandNames.EDIT_BOARD.name);

        ServerCaller.<Dtos.BoardDto, Dtos.BoardDto>sendRequest(
                toStore,
                Dtos.BoardDto.class,
                new ResourceClosingCallback<Dtos.BoardDto>(BoardEditingComponent.this) {

                    @Override
                    public void success(Dtos.BoardDto response) {
                        MessageBus.sendMessage(new BoardEditedMessage(response, this));
                        MessageBus.sendMessage(new BoardChangedMessage(response, this));
                    }
                }
        );
	}

	@Override
	public void setDto(Dtos.BoardWithProjectsDto dto) {
		this.boardDto = dto.getBoard();
	}

	@Override
	public void messageArrived(Message<Dtos.BoardDto> message) {
		if (message.getPayload().getId().equals(boardDto.getId())) {
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
