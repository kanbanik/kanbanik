package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public abstract class BoardGuiBuilder {
	
	private static final BoardStyle style = KanbanikResources.INSTANCE.boardStyle();
	
	public void buildBoard(
			WorkflowitemDto workflowitem,
			ProjectDto project,
			FlexTable table,
			PickupDragController dragController, 
			int row, 
			int column) {
		style.ensureInjected();
		
		if (workflowitem == null) {
			return;
		}
		
		WorkflowitemDto currentItem = workflowitem;

		String height = calculatVerticalItemsHeight(currentItem);
		while (true) {
			if (currentItem.getChild() != null) {
				FlexTable childTable = new FlexTable();
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, childTable);
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
				setupTdHeight(table, row, column, height);
				buildBoard(currentItem.getChild(), project, childTable, dragController, 0, 0);
			} else {
				Widget taskContainer = createWorkflowitemPlaceContentWidget(dragController, currentItem, project);
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, taskContainer);
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
				setupTdHeight(table, row, column, height);
			}

			if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				column++;
			} else if (currentItem.getItemType() == ItemType.VERTICAL) {
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

	private void setupTdHeight(FlexTable table, int row, int column, String height) {
		// TODO - find a less hacky way of styling this
		Element td = table.getCellFormatter().getElement(row, column);
		DOM.setElementProperty(td, "height", height);
	}
	
	private String calculatVerticalItemsHeight(WorkflowitemDto item) {
		int deepness = findDeepness(item);
		int height = 100;
		if (deepness != 0) {
			height = (int) 100 / deepness;
		}
		
		return height + "%";
	}
	
	private int findDeepness(WorkflowitemDto item) {
		int deep = 0;
		WorkflowitemDto dto = item;
		do  {
			if (dto.getItemType() == ItemType.VERTICAL) {
				deep ++;
			}
			
			dto = dto.getNextItem();
		} while (dto != null);
		
		return deep;
	}
	
	protected abstract Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project);
	
	protected abstract Widget createWorkflowitemPlace(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project, Widget childTable);
}
