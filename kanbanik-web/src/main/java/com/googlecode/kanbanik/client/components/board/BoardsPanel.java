package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.MarkBoardsAsDirtyMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.FilterChangedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

public class BoardsPanel extends Composite implements ModulesLifecycleListener, MessageListener<Dtos.BoardDto> {

    interface MyUiBinder extends UiBinder<Widget, BoardsPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Panel boardPanel;

    @UiField
    Panel dirtyPanel;
	
	public BoardsPanel() {
		initWidget(uiBinder.createAndBindUi(this));

        MessageBus.registerListener(MarkBoardsAsDirtyMessage.class, this);

        new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);

        dirtyPanel.setVisible(false);
	}
	
	public void addBoard(Composite board) {
		boardPanel.add(board);
	}

    @Override
    public void activated() {
        MessageBus.registerOnce(MarkBoardsAsDirtyMessage.class, this);
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(MarkBoardsAsDirtyMessage.class, this);
    }

    @Override
    public void messageArrived(Message<Dtos.BoardDto> message) {
        dirtyPanel.setVisible(true);
    }
}
