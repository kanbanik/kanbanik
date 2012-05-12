package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.DragContext;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowEditingComponent extends Composite {
	
	interface MyUiBinder extends UiBinder<Widget, WorkflowEditingComponent> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	Panel board;
	
	@UiField
	Panel palette;

	private AbsolutePanel panelWithDraggabls;

	private BoardDto boardDto;
	
	public WorkflowEditingComponent(BoardWithProjectsDto boardWithProjects) {
		this.boardDto = boardWithProjects.getBoard();
		initWidget(uiBinder.createAndBindUi(this));
		renderBoard();
	}
	
	private void renderBoard() {
		if (panelWithDraggabls != null) {
			board.remove(panelWithDraggabls);
		}
		
		FlexTable table = new FlexTable();
		table.setBorderWidth(1);
		panelWithDraggabls = new AbsolutePanel();
		PickupDragController dragController = new PickupDragController(
				panelWithDraggabls, false);
		panelWithDraggabls.add(table);
		
		buildBoard(
				null,
				boardDto.getRootWorkflowitem(),
				null,
				table,
				dragController,
				0,
				0
		);
		
		board.add(panelWithDraggabls);
	}
	
	
	public void buildBoard(
			WorkflowitemDto parentWorkflowitem,
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

		table.setWidget(row, column, createDropTarget(dragController, parentWorkflowitem, currentItem, Position.BEFORE));
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
				buildBoard(currentItem, currentItem.getChild(), project, childTable, dragController, 0, 0);
			} else {
				Widget taskContainer = createWorkflowitemPlaceContentWidget(dragController, currentItem, project);
				table.setWidget(row, column, createWorkflowitemPlace(dragController, currentItem, project, taskContainer));
			}

			if (currentItem.getItemType() == ItemType.VERTICAL) {
				column++;
				table.setWidget(row, column, createDropTarget(dragController, parentWorkflowitem, currentItem, Position.AFTER));
				column++;
			} else if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				row++;
				table.setWidget(row, column, createDropTarget(dragController, parentWorkflowitem, currentItem, Position.AFTER));
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

	private Panel createDropTarget(PickupDragController dragController, WorkflowitemDto parentItem, WorkflowitemDto currentItem, Position position) {
		FlowPanel dropTarget = new FlowPanel();
		Label dropLabel = new Label("drop");
		dropTarget.add(dropLabel);
		DropController dropController = new WorkflowEditingDropController(dropTarget, parentItem, currentItem, position);
		dragController.registerDropController(dropController);
		return dropTarget;
	}
	
	class WorkflowEditingDropController extends FlowPanelDropController {
		
		private final WorkflowitemDto parent;
		
		private final WorkflowitemDto currentItem;
		
		private final Position position;

		public WorkflowEditingDropController(
				FlowPanel dropTarget,
				WorkflowitemDto parent,
				WorkflowitemDto currentItem, 
				Position position) {
			super(dropTarget);
			this.parent = parent;
			this.currentItem = currentItem;
			this.position = position;
		}
		
		@Override
		public void onDrop(DragContext context) {
			super.onDrop(context);
			
			if (context.selectedWidgets.size() > 1) {
				throw new UnsupportedOperationException("Only one workflowitem can be dragged at a time");
			}
			
			Widget w = context.selectedWidgets.iterator().next();
			if (!(w instanceof WorkflowitemWidget)) {
				return;
			}
			
			WorkflowitemDto droppedItem = ((WorkflowitemWidget) w).getWorkflowitem();
			WorkflowitemDto parentToSend = createParentToSend(droppedItem);
			WorkflowitemDto nextItem = findNextItem();
			droppedItem.setNextItem(nextItem);
			
			ServerCommandInvokerManager.getInvoker().<EditWorkflowParams, VoidParams> invokeCommand(
					ServerCommand.EDIT_WORKFLOW,
					new EditWorkflowParams(parentToSend, droppedItem),
					new KanbanikAsyncCallback<VoidParams>() {

						@Override
						public void success(VoidParams result) {
							// I know, this is not really efficient...
							refreshBoard();
						}

					});
		}
		
		private void refreshBoard() {
			ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardDto>, SimpleParams<BoardDto>> invokeCommand(
					ServerCommand.GET_BOARD,
					new SimpleParams<BoardDto>(boardDto),
					new KanbanikAsyncCallback<SimpleParams<BoardDto>>() {

						@Override
						public void success(SimpleParams<BoardDto> result) {
							boardDto = result.getPayload();
							renderBoard();
						}

					});			
		}
		
		private WorkflowitemDto findNextItem() {
			if (position == Position.BEFORE) {
				return currentItem;
			}
			
			return currentItem.getNextItem();
		}

		private WorkflowitemDto createParentToSend(WorkflowitemDto droppedItem) {
			if (parent == null) {
				return null;
			}
			
			if (position == Position.AFTER) {
				// I was not dropped it before the first if I have dropped it after something
				return null;
			}
			
			WorkflowitemDto prevChildOfParent = parent.getChild();
			if (!currentItem.getId().equals(prevChildOfParent.getId())) {
				// when I did not dropped it before a first child, the parent's first child don't need to be updated 
				return null;
			}
			
			parent.setChild(droppedItem);
			return parent;
		}
		
	}

	private Widget createWorkflowitemPlace(
			PickupDragController dragController,
			WorkflowitemDto currentItem, 
			ProjectDto project,
			Widget childTable) {
		
		FlowPanel dropTarget = new FlowPanel();
		DropController dropController = new DropDisablingDropController(dropTarget);
		dragController.registerDropController(dropController);
		WorkflowitemWidget workflowitemWidget = new WorkflowitemWidget(currentItem, childTable);
		dragController.makeDraggable(workflowitemWidget, workflowitemWidget.getHeader());
		dropTarget.add(workflowitemWidget);

		return dropTarget;
	}
	
	enum Position {
		BEFORE, AFTER;
	}	
	
	class DropDisablingDropController extends FlowPanelDropController {

		public DropDisablingDropController(FlowPanel dropTarget) {
			super(dropTarget);
		}
		
		@Override
		public void onEnter(DragContext context) {
		}
		
		@Override
		public void onLeave(DragContext context) {
		}
		
		@Override
		public void onMove(DragContext context) {
		}
	}
	
	private Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project) {
		return new SimplePanel();
	}
}
