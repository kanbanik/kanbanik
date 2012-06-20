package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowEditingComponent extends Composite implements
		ModulesLifecycleListener, MessageListener<RefreshBoardsRequestMessage> {

	interface MyUiBinder extends UiBinder<Widget, WorkflowEditingComponent> {
	}
	
	public interface Style extends CssResource {
        String boardStyle();
        String dropTargetStyle();
		String palettePanelStyle();
		String paletteHeaderStyle();
		String paletteDescriptionStyle();
    }


	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField 
	Style style;
	
	@UiField
	Panel board;

	private AbsolutePanel panelWithDraggabls;

	private BoardDto boardDto;

	public WorkflowEditingComponent(BoardWithProjectsDto boardWithProjects) {
		this.boardDto = boardWithProjects.getBoard();
		initWidget(uiBinder.createAndBindUi(this));

		renderBoard();
		
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		MessageBus.registerListener(RefreshBoardsRequestMessage.class, this);
	}

	private void initAndAddPalette(PickupDragController dragController) {

		WorkflowitemDto horizontal = new WorkflowitemDto();
		horizontal.setBoard(boardDto);
		horizontal.setItemType(ItemType.HORIZONTAL);
		horizontal.setName("Horizontal Item");
		horizontal.setWipLimit(0);
		
		WorkflowitemDto vertical = new WorkflowitemDto();
		vertical.setBoard(boardDto);
		vertical.setItemType(ItemType.VERTICAL);
		vertical.setName("Vertical Item");
		horizontal.setWipLimit(0);
		
		WorkflowItemPalette paletteContent = new WorkflowItemPalette(dragController);
		
		paletteContent.addWithDraggable(new PaletteWorkflowitemWidget(horizontal));
		paletteContent.addWithDraggable(new PaletteWorkflowitemWidget(vertical));
		FlowPanel designPanel = new FlowPanel();
		
		Label headerLabel = new Label("Workflowitem Palette");
		headerLabel.setStyleName(style.paletteHeaderStyle());
		
		Label descriptionLabel = new Label("Drag and drop workflowitems from palette to workflow");
		descriptionLabel.setStyleName(style.paletteDescriptionStyle());
		
		designPanel.add(headerLabel);
		designPanel.add(descriptionLabel);
		designPanel.add(paletteContent);
		designPanel.setStyleName(style.palettePanelStyle());
		panelWithDraggabls.add(designPanel);
	}

	private void renderBoard() {
		if (panelWithDraggabls != null) {
			board.remove(panelWithDraggabls);
		}

		FlexTable table = new FlexTable();
		table.setStyleName(style.boardStyle());
		
		panelWithDraggabls = new AbsolutePanel();
		PickupDragController dragController = new PickupDragController(
				panelWithDraggabls, false);
		
		Label boardLabel = new Label("Workflow");
		panelWithDraggabls.add(boardLabel);
		
		panelWithDraggabls.add(table);

		buildBoard(null, boardDto.getRootWorkflowitem(), null, table,
				dragController, 0, 0);

		// default DTO
		if (boardDto.getRootWorkflowitem() == null) {
			table.setWidget(
					0,
					0,
					createDropTarget(dragController, 
							null,
							null, 
							Position.BEFORE,
							KanbanikResources.INSTANCE.insideDropArrowImage()));
		}
		
		board.add(panelWithDraggabls);
		initAndAddPalette(dragController);
	}

	public void buildBoard(WorkflowitemDto parentWorkflowitem,
			WorkflowitemDto workflowitem, ProjectDto project, FlexTable table,
			PickupDragController dragController, int row, int column) {
		if (workflowitem == null) {
			return;
		}
		WorkflowitemDto currentItem = workflowitem;

		if (currentItem.getItemType() == ItemType.HORIZONTAL) {
			table.setWidget(
					row,
					column,
					createDropTarget(dragController, parentWorkflowitem,
							currentItem, Position.BEFORE,
							KanbanikResources.INSTANCE.rightDropArrowImage()));
			column++;
		} else if (currentItem.getItemType() == ItemType.VERTICAL) {
			table.setWidget(
					row,
					column,
					createDropTarget(dragController, parentWorkflowitem,
							currentItem, Position.BEFORE,
							KanbanikResources.INSTANCE.downDropArrowImage()));

			row++;
		} else {
			throw new IllegalStateException("Unsupported item type: '"
					+ currentItem.getItemType() + "'");
		}

		while (true) {
			if (currentItem.getChild() != null) {
				// this one has a child, so does not have a drop target in it's
				// body (content)
				FlexTable childTable = new FlexTable();
				childTable.setStyleName(style.boardStyle());

				table.setWidget(
						row,
						column,
						createWorkflowitemPlace(dragController, currentItem,
								project, childTable));
				buildBoard(currentItem, currentItem.getChild(), project,
						childTable, dragController, 0, 0);
			} else {
				// this one does not have a child yet, so create a drop target
				// so it can have in the future
				Panel dropTarget = createDropTarget(dragController,
						currentItem, null, Position.INSIDE,
						KanbanikResources.INSTANCE.insideDropArrowImage());
				table.setWidget(
						row,
						column,
						createWorkflowitemPlace(dragController, currentItem,
								project, dropTarget));
			}

			if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				column++;
				table.setWidget(
						row,
						column,
						createDropTarget(dragController, parentWorkflowitem,
								currentItem, Position.AFTER, KanbanikResources.INSTANCE.rightDropArrowImage()));
				column++;
			} else if (currentItem.getItemType() == ItemType.VERTICAL) {
				row++;
				table.setWidget(
						row,
						column,
						createDropTarget(dragController, parentWorkflowitem,
								currentItem, Position.AFTER, KanbanikResources.INSTANCE.downDropArrowImage()));
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

	private Panel createDropTarget(PickupDragController dragController,
			WorkflowitemDto contextItem, WorkflowitemDto currentItem,
			Position position, ImageResource image) {
		FlowPanel dropTarget = new FlowPanel();
		dropTarget.setStyleName(style.dropTargetStyle());
		dropTarget.add(new Image(image));
		DropController dropController = new WorkflowEditingDropController(
				dropTarget, contextItem, currentItem, position);
		dragController.registerDropController(dropController);
		return dropTarget;
	}

	private Widget createWorkflowitemPlace(PickupDragController dragController,
			WorkflowitemDto currentItem, ProjectDto project, Widget childTable) {

		FlowPanel dropTarget = new FlowPanel();
		DropController dropController = new DropDisablingDropController(
				dropTarget);
		dragController.registerDropController(dropController);
		WorkflowitemWidget workflowitemWidget = new WorkflowitemWidget(
				currentItem, childTable);
		dragController.makeDraggable(workflowitemWidget,
				workflowitemWidget.getHeader());
		dropTarget.add(workflowitemWidget);

		return dropTarget;
	}

	enum Position {
		BEFORE, AFTER, INSIDE;
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

	public void activated() {
		if (!MessageBus.listens(RefreshBoardsRequestMessage.class, this)) {
			MessageBus
					.registerListener(RefreshBoardsRequestMessage.class, this);
		}

	}

	public void deactivated() {
		MessageBus.unregisterListener(RefreshBoardsRequestMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	public void messageArrived(Message<RefreshBoardsRequestMessage> message) {
		// I know, this is not really efficient...
		// One day it should be improved
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
		ServerCommandInvokerManager
				.getInvoker()
				.<SimpleParams<BoardDto>, SimpleParams<BoardDto>> invokeCommand(
						ServerCommand.GET_BOARD,
						new SimpleParams<BoardDto>(boardDto),
						new KanbanikAsyncCallback<SimpleParams<BoardDto>>() {

							@Override
							public void success(SimpleParams<BoardDto> result) {
								boardDto = result.getPayload();
								renderBoard();
							}

						});
		}});
	}

}
