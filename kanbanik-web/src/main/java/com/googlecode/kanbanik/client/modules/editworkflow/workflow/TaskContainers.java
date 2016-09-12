package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.board.TableTaskContainer;
import com.googlecode.kanbanik.client.components.board.TaskContainer;
import com.googlecode.kanbanik.client.components.board.TicketTaskContainer;

public class TaskContainers {

    private TableTaskContainer tableTaskContainer;

    private TicketTaskContainer ticketTaskContainer;

    private TaskContainer current;

    public TaskContainers(TableTaskContainer tableTaskContainer, TicketTaskContainer ticketTaskContainer) {
        this.tableTaskContainer = tableTaskContainer;
        this.ticketTaskContainer = ticketTaskContainer;

        this.current = ticketTaskContainer;
    }

    public TableTaskContainer getTableTaskContainer() {
        return tableTaskContainer;
    }

    public TicketTaskContainer getTicketTaskContainer() {
        return ticketTaskContainer;
    }

    public TaskContainer getCurrent() {
        return current;
    }

    public TaskContainer switchView() {
        if (current == tableTaskContainer) {
            current = ticketTaskContainer;
        } else {
            current = tableTaskContainer;
        }

        return current;
    }
}
