package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.board.TableTaskContainer;
import com.googlecode.kanbanik.client.components.board.TaskContainer;
import com.googlecode.kanbanik.client.components.board.TicketTaskContainer;
import com.googlecode.kanbanik.client.components.board.TaskMovingDropController;
import com.googlecode.kanbanik.client.components.board.WorkflowitemPlace;

import java.util.ArrayList;
import java.util.List;

public class BoardGuiBuilder {
	
	private static final BoardStyle style = KanbanikResources.INSTANCE.boardStyle();

	public BoardGuiBuilder() {
		style.ensureInjected();
	}

	public void buildBoard(
            WipLimitGuard wipLimitGuard,
            List<ExtendedWorkflowitem> children,
            ExtendedWorkflowitem parentItem,
            Dtos.WorkflowDto workflow,
			Dtos.ProjectDto project,
			FlexTable table,
			PickupDragController dragController, 
			int row, 
			int column) {
		
		if (workflow == null) {
			return;
		}
		
		Dtos.WorkflowDto currentDto = workflow;

		for (Dtos.WorkflowitemDto currentItem : currentDto.getWorkflowitems()) {

            ExtendedWorkflowitem extendedWorkflowitem = new ExtendedWorkflowitem(currentItem, parentItem);
            wipLimitGuard.addItem(currentItem.getId(), extendedWorkflowitem);
            children.add(extendedWorkflowitem);

			if (!currentItem.getNestedWorkflow().getWorkflowitems().isEmpty()) {
				FlexTable childTable = new FlexTable();
				
				setupBoard(childTable, workflow.getBoard());
				
                Widget workflowitemPlace = new WorkflowitemPlace(
                        currentItem,
                        project,
                        childTable,
                        dragController,
                        workflow.getBoard());

				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
                List<ExtendedWorkflowitem> nestedChildren = new ArrayList<>();
                extendedWorkflowitem.setChildren(nestedChildren);
				buildBoard(wipLimitGuard, nestedChildren, extendedWorkflowitem, currentItem.getNestedWorkflow(), project, childTable, dragController, 0, 0);
			} else {
				TaskContainers taskContainers = createWorkflowitemPlaceContentWidget(dragController, currentItem, project, workflow.getBoard());
                Widget workflowitemPlace = new WorkflowitemPlace(
                        currentItem,
                        project,
                        taskContainers,
                        dragController,
                        workflow.getBoard());

				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
                extendedWorkflowitem.setChildren(new ArrayList<ExtendedWorkflowitem>());
                extendedWorkflowitem.setTaskContainer(taskContainers.getCurrent());
                (taskContainers.getTableTaskContainer()).setWipLimitGuard(wipLimitGuard);
                (taskContainers.getTicketTaskContainer()).setWipLimitGuard(wipLimitGuard);
				setupBoard(table, workflow.getBoard());
			}

			if (currentItem.getItemType().equals(Dtos.ItemType.HORIZONTAL.getType())) {
				column++;
			} else if (currentItem.getItemType().equals(Dtos.ItemType.VERTICAL.getType())) {
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}
		}

	}

	
	private void setupBoard(FlexTable table, Dtos.BoardDto board) {
        Dtos.WorkflowVerticalSizing sizing = Dtos.WorkflowVerticalSizing.from(board.getWorkflowVerticalSizing());
		if (sizing == Dtos.WorkflowVerticalSizing.BALANCED) {
			table.addStyleName("balanced-table");
		} else if (sizing == Dtos.WorkflowVerticalSizing.MIN_POSSIBLE) {
			table.addStyleName("not-balanced-table");
		}
		
		table.getElement().getStyle().setProperty("background", "#2e2e2e url(background.png)");

		table.setWidth("100%");
	}

    protected TaskContainers createWorkflowitemPlaceContentWidget(
            PickupDragController dragController,
            Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Dtos.BoardDto board) {

        TableTaskContainer tableTaskContainer = new TableTaskContainer(board, currentItem);
        TicketTaskContainer ticketTaskContainer = new TicketTaskContainer(board, currentItem);

        new TaskMovingDropController(
                tableTaskContainer, currentItem, project, board, dragController);

        DropController ticketDropController = new TaskMovingDropController(
                ticketTaskContainer, currentItem, project, board, dragController);
        dragController.registerDropController(ticketDropController);

        return new TaskContainers(tableTaskContainer, ticketTaskContainer);
    }

}

