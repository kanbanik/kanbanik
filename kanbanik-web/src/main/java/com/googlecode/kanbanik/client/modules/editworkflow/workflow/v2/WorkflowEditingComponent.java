package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class WorkflowEditingComponent extends Composite {
	
	interface MyUiBinder extends UiBinder<Widget, WorkflowEditingComponent> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	Panel board;
	
	@UiField
	Panel palette;
	
	public WorkflowEditingComponent(BoardWithProjectsDto boardWithProjects) {
		initWidget(uiBinder.createAndBindUi(this));
		FlexTable table = new FlexTable();
		table.setBorderWidth(1);
		
		AbsolutePanel panelWithDraggabls = new AbsolutePanel();
		PickupDragController dragController = new PickupDragController(
				panelWithDraggabls, false);
		panelWithDraggabls.add(table);
		
		buildBoard(
				boardWithProjects.getBoard().getRootWorkflowitem(),
				null,
				table,
				dragController,
				0,
				0
		);
		
		board.add(panelWithDraggabls);
	}
	
	
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

		table.setWidget(row, column, createDropTarget(dragController));
		if (currentItem.getItemType() == ItemType.VERTICAL) {
			column++;
		} else if (currentItem.getItemType() == ItemType.HORIZONTAL) {
			row++;
		} else {
			throw new IllegalStateException("Unsupported item type: '"
					+ currentItem.getItemType() + "'");
		}
		
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
				table.setWidget(row, column, createDropTarget(dragController));
				column++;
			} else if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				row++;
				table.setWidget(row, column, createDropTarget(dragController));
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

	private Panel createDropTarget(PickupDragController dragController) {
		FlowPanel dropTarget = new FlowPanel();
//		dropTarget.setWidth("3px");
		DropController dropController = new FlowPanelDropController(dropTarget);
		dragController.registerDropController(dropController);
		return dropTarget;
	}
	
	private Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project) {
		// will than handle the drag/drop
		return new SimplePanel();
	}

	private Widget createWorkflowitemPlace(
			PickupDragController dragController,
			WorkflowitemDto currentItem, 
			ProjectDto project,
			Widget childTable) {
		
		Panel dropTarget = createDropTarget(dragController);
		WorkflowitemWidget workflowitemWidget = new WorkflowitemWidget(currentItem, childTable);
		dragController.makeDraggable(workflowitemWidget, workflowitemWidget.getHeader());
		dropTarget.add(workflowitemWidget);
		return dropTarget;
	}
}
