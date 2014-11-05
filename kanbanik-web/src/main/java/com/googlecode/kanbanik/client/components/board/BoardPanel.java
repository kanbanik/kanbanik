package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.GetAllBoardsRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.GetAllBoardsResponseMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.FilterChangedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

import java.util.List;

public class BoardPanel extends Composite implements ModulesLifecycleListener, MessageListener<Dtos.BoardDto> {
	
	interface MyUiBinder extends UiBinder<Widget, BoardPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField(provided=true)
	Panel projects;

    private Dtos.BoardDto boardDto;

    @UiField
	Label boardName;

    private FilterChangedListener filterChangedListener = new FilterChangedListener();

	public BoardPanel(Panel projects, Dtos.BoardDto boardDto) {

		this.projects = projects;
        this.boardDto = boardDto;

        initWidget(uiBinder.createAndBindUi(this));
		
		boardName.setText(boardDto.getName());

        MessageBus.registerListener(GetAllBoardsRequestMessage.class, this);
        MessageBus.registerListener(FilterChangedMessage.class, filterChangedListener);

        new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
	}

    @Override
    public void activated() {
        if (!MessageBus.listens(GetAllBoardsRequestMessage.class, this)) {
            MessageBus.registerListener(GetAllBoardsRequestMessage.class, this);
        }

        if (!MessageBus.listens(FilterChangedMessage.class, filterChangedListener)) {
            MessageBus.registerListener(FilterChangedMessage.class, filterChangedListener);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(GetAllBoardsRequestMessage.class, this);
        MessageBus.unregisterListener(FilterChangedMessage.class, filterChangedListener);
    }

    @Override
    public void messageArrived(Message<Dtos.BoardDto> message) {
        MessageBus.sendMessage(new GetAllBoardsResponseMessage(boardDto, this));
    }

    class FilterChangedListener implements MessageListener<BoardsFilter> {

        @Override
        public void messageArrived(Message<BoardsFilter> message) {
            List<Dtos.Filtered<Dtos.BoardDto>> visibleBoards = message.getPayload().getFilterDataDto().getBoards();

            setVisible(false);

            if (visibleBoards == null || visibleBoards.size() == 0) {
                return;
            }

            for (Dtos.Filtered<Dtos.BoardDto> visibleBoard : visibleBoards) {
                if (visibleBoard.getData().getId().equals(boardDto.getId())) {
                    setVisible(true);
                    break;
                }
            }
        }
    }

}