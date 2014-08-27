package com.googlecode.kanbanik.client.modules;

import com.google.gwt.user.client.ui.FlexTable;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.FilterChangedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

public class ProjectGui extends FlexTable implements ModulesLifecycleListener {

    private Dtos.BoardDto board;
    private Dtos.ProjectDto project;

    private FilterChangedListener filterChangedListener = new FilterChangedListener();

    public ProjectGui(Dtos.BoardDto board, Dtos.ProjectDto project) {
        this.board = board;
        this.project = project;

        MessageBus.registerListener(FilterChangedMessage.class, filterChangedListener);

        new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
    }

    @Override
    public void activated() {
        if (!MessageBus.listens(FilterChangedMessage.class, filterChangedListener)) {
            MessageBus.registerListener(FilterChangedMessage.class, filterChangedListener);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(FilterChangedMessage.class, filterChangedListener);
    }

    class FilterChangedListener implements MessageListener<BoardsFilter> {

        @Override
        public void messageArrived(Message<BoardsFilter> message) {
            boolean visible = message.getPayload().projectOnBoardMatches(project, board);
            setVisible(visible);
        }
    }
}
