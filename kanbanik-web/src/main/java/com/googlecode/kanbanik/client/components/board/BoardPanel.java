package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.GetBoardsRequestMessage;
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

    @UiField
    PushButton linkButton;

    private FilterChangedListener filterChangedListener = new FilterChangedListener();

	public BoardPanel(Panel projects, final Dtos.BoardDto boardDto, List<Dtos.ProjectDto> projectsOnBoard) {

		this.projects = projects;
        this.boardDto = boardDto;

        initWidget(uiBinder.createAndBindUi(this));
//        , gwt-Button:active, gwt-Button:hover, gwt-Button[disabled], gwt-Button[disabled]:hover
        linkButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.chainImage()));

        linkButton.addClickHandler(new LinkClickHandler(boardDto, projectsOnBoard));
        linkButton.removeStyleName("gwt-Button");

		boardName.setText(boardDto.getName());

        MessageBus.registerListener(GetBoardsRequestMessage.class, this);
        MessageBus.registerListener(FilterChangedMessage.class, filterChangedListener);

        new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
	}

    @Override
    public void activated() {
        if (!MessageBus.listens(GetBoardsRequestMessage.class, this)) {
            MessageBus.registerListener(GetBoardsRequestMessage.class, this);
        }

        if (!MessageBus.listens(FilterChangedMessage.class, filterChangedListener)) {
            MessageBus.registerListener(FilterChangedMessage.class, filterChangedListener);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(GetBoardsRequestMessage.class, this);
        MessageBus.unregisterListener(FilterChangedMessage.class, filterChangedListener);
    }

    @Override
    public void messageArrived(Message<Dtos.BoardDto> message) {
        if (message instanceof GetBoardsRequestMessage) {
            if (((GetBoardsRequestMessage) message).getFilter().apply(boardDto)) {
                MessageBus.sendMessage(new GetAllBoardsResponseMessage(boardDto, this));
            }
        }
    }

    class FilterChangedListener implements MessageListener<BoardsFilter> {

        @Override
        public void messageArrived(Message<BoardsFilter> message) {
            boolean matches = message.getPayload().boardMatches(boardDto, isVisible());
            setVisible(matches);
        }

    }

}