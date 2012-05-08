package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.board.WorkflowitemPlace;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public abstract class BoardGuiBuilder {
	
	public void buildBoard(
			WorkflowitemDto workflowitem,
			ProjectDto project,
			FlexTable table,
			PickupDragController dragController, 
			int row, 
			int column) {
		if (workflowitem == null) {
			return;
		}
		WorkflowitemDto currentItem = workflowitem;

		while (true) {
			if (currentItem.getChild() != null) {
				FlexTable childTable = new FlexTable();
				childTable.setBorderWidth(1);

				table.setWidget(row, column, createWorkflowitemPlace(dragController, currentItem, project, childTable));
				buildBoard(currentItem.getChild(), project, childTable, dragController, 0, 0);
			} else {
				Widget taskContainer = createWorkflowitemPlaceContentWidget(dragController, currentItem, project);
				table.setWidget(row, column, createWorkflowitemPlace(dragController, currentItem, project, taskContainer));
			}

			if (currentItem.getItemType() == ItemType.VERTICAL) {
				column++;
			} else if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}

			currentItem = currentItem.getNextItem();
			if (currentItem == null) {
				break;
			}

		}

	}
	
	protected abstract Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project);
	
	protected abstract Widget createWorkflowitemPlace(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project, Widget childTable);
}
