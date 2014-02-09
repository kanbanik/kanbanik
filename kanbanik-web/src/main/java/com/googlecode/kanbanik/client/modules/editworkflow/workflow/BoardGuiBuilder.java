package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing;
import com.googlecode.kanbanik.dto.WorkflowDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public abstract class BoardGuiBuilder {
	
	private static final BoardStyle style = KanbanikResources.INSTANCE.boardStyle();
	
	public BoardGuiBuilder() {
		style.ensureInjected();
	}
	
	public void buildBoard(
			WorkflowDto workflow,
			ProjectDto project,
			FlexTable table,
			PickupDragController dragController, 
			int row, 
			int column) {
		
		if (workflow == null) {
			return;
		}
		
		WorkflowDto currentDto = workflow;
		
		for (WorkflowitemDto currentItem : currentDto.getWorkflowitems()) {
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

			if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				column++;
			} else if (currentItem.getItemType() == ItemType.VERTICAL) {
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}
		}

	}

	
	private void setupBoard(FlexTable table, BoardDto board) {
		WorkfloVerticalSizing sizing = board.getWorkfloVerticalSizing();
		if (sizing == WorkfloVerticalSizing.BALANCED) {
			table.addStyleName("balanced-table");
		} else if (sizing == WorkfloVerticalSizing.MIN_POSSIBLE) {
			table.addStyleName("not-balanced-table");
		}
		
		table.getElement().getStyle().setBackgroundColor("#FBFBFB");
		table.setWidth("100%");
	}
	
	protected abstract Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project, BoardDto board);
	
	protected abstract Widget createWorkflowitemPlace(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project, Widget childTable, BoardDto board);
}
