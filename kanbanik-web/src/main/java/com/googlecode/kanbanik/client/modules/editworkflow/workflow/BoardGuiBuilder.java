package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.Dtos;

public abstract class BoardGuiBuilder {
	
	private static final BoardStyle style = KanbanikResources.INSTANCE.boardStyle();
	
	public BoardGuiBuilder() {
		style.ensureInjected();
	}
	
	public void buildBoard(
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
			if (currentItem.getNestedWorkflow().getWorkflowitems().size() > 0) {
				FlexTable childTable = new FlexTable();
				
				setupBoard(childTable, workflow.getBoard());
				
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, childTable, workflow.getBoard());
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
				buildBoard(currentItem.getNestedWorkflow(), project, childTable, dragController, 0, 0);
			} else {
				Widget taskContainer = createWorkflowitemPlaceContentWidget(dragController, currentItem, project, workflow.getBoard());
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, taskContainer, workflow.getBoard());
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
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
		
		table.getElement().getStyle().setBackgroundColor("#FBFBFB");
		table.setWidth("100%");
	}
	
	protected abstract Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Dtos.BoardDto board);
	
	protected abstract Widget createWorkflowitemPlace(PickupDragController dragController, Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Widget childTable, Dtos.BoardDto board);
}
