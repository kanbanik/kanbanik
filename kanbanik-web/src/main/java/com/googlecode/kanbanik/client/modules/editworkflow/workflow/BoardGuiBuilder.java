package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
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
		
		boolean isBalanced = workflow.getBoard().isBalanceWorkflowitems();
		
		WorkflowDto currentDto = workflow;
		
		for (WorkflowitemDto currentItem : currentDto.getWorkflowitems()) {
			if (currentItem.getNestedWorkflow().getWorkflowitems().size() > 0) {
				FlexTable childTable = new FlexTable();
				
				if (isBalanced) {
					childTable.addStyleName("balanced-table");
				} else {
					childTable.addStyleName("not-balanced-table");
				}
				
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, childTable);
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
				buildBoard(currentItem.getNestedWorkflow(), project, childTable, dragController, 0, 0);
			} else {
				Widget taskContainer = createWorkflowitemPlaceContentWidget(dragController, currentItem, project);
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, taskContainer);
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
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

	protected abstract Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project);
	
	protected abstract Widget createWorkflowitemPlace(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project, Widget childTable);
}
