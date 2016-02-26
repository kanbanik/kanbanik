package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

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
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

public class WorkflowEditingComponent extends Composite implements
		ModulesLifecycleListener, MessageListener<Dtos.BoardDto> {

	interface MyUiBinder extends UiBinder<Widget, WorkflowEditingComponent> {
	}
	
	public interface Style extends CssResource {
        String dropTargetStyle();
		String palettePanelStyle();
		String headerTextStyle();
		String tablePanelStyle();
    }


	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField 
	Style style;
	
	@UiField
	Panel board;

	private AbsolutePanel panelWithDraggabls;

	private Dtos.BoardDto boardDto;

	public WorkflowEditingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	public void initialize(Dtos.BoardWithProjectsDto boardWithProjects) {
		this.boardDto = boardWithProjects.getBoard();
		renderBoard();
		
		registerListeners();
	}
	
	private void initAndAddPalette(PickupDragController dragController, FlowPanel mainContentPanel) {
		if (boardDto == null) {
			return;
		}
		
		Dtos.WorkflowitemDto horizontal = DtoFactory.workflowitemDto();
		horizontal.setParentWorkflow(boardDto.getWorkflow());
		horizontal.setNestedWorkflow(createNestedWorkflow());
		horizontal.setItemType(Dtos.ItemType.HORIZONTAL.getType());
		horizontal.setName("Horizontal Item");
		horizontal.setWipLimit(-1);
		horizontal.setVerticalSize(-1);
        horizontal.setVersion(1);

        Dtos.WorkflowitemDto vertical = DtoFactory.workflowitemDto();
		vertical.setParentWorkflow(boardDto.getWorkflow());
		vertical.setNestedWorkflow(createNestedWorkflow());
		vertical.setItemType(Dtos.ItemType.VERTICAL.getType());
		vertical.setName("Vertical Item");
		vertical.setWipLimit(-1);
		vertical.setVerticalSize(-1);
        vertical.setVersion(1);
		
		WorkflowItemPalette paletteContent = new WorkflowItemPalette(dragController);
		paletteContent.addWithDraggable(new PaletteWorkflowitemWidget(horizontal, imageResourceAsPanel(KanbanikResources.INSTANCE.rightDropArrowImage())));
		paletteContent.addWithDraggable(new PaletteWorkflowitemWidget(vertical, imageResourceAsPanel(KanbanikResources.INSTANCE.downDropArrowImage())));
		FlowPanel designPanel = new FlowPanel();
		
		Label headerLabel = new Label("Drag and drop workflowitems from palette to workflow");
		headerLabel.setStyleName(style.headerTextStyle());

		designPanel.add(headerLabel);
		designPanel.add(paletteContent);
		designPanel.setStyleName(style.palettePanelStyle());
		
		mainContentPanel.add(designPanel);
	}

	private Dtos.WorkflowDto createNestedWorkflow() {
		Dtos.WorkflowDto dto = DtoFactory.workflowDto();
		dto.setBoard(boardDto);
		return dto;
	}
	
	private Panel imageResourceAsPanel(ImageResource image) {
		Panel panel = new FlowPanel();
		panel.add(new Image(image));
		return panel;
	}
	
	private void renderBoard() {
		if (boardDto == null) {
			return;
		}
		if (panelWithDraggabls != null) {
			board.remove(panelWithDraggabls);
		}
		
		FlexTable table = new FlexTable();
		table.setStyleName("boards-board");
		FlowPanel mainContentPanel = new FlowPanel();
		FlowPanel tableDesignPanel = new FlowPanel();
		tableDesignPanel.addStyleName(style.tablePanelStyle());
		Label headerLabel = new Label("Workflow of board: " + boardDto.getName());
		headerLabel.setStyleName(style.headerTextStyle());
		tableDesignPanel.add(headerLabel);
		tableDesignPanel.add(table);
		
		panelWithDraggabls = new AbsolutePanel();
		PickupDragController dragController = new PickupDragController(panelWithDraggabls, false);
        dragController.setBehaviorDragStartSensitivity(3);
        dragController.setBehaviorCancelDocumentSelections(true);
		
		mainContentPanel.add(tableDesignPanel);
		panelWithDraggabls.add(mainContentPanel);

		buildBoard(null, boardDto.getWorkflow(), null, table,
				dragController, 0, 0);

		// default DTO
		if (boardDto.getWorkflow().getWorkflowitems().isEmpty()) {
			table.setWidget(
					0,
					0,
					createDropTarget(dragController, 
							boardDto.getWorkflow(),
							null, 
							Position.BEFORE,
							KanbanikResources.INSTANCE.insideDropArrowImage()));
		}
		
		board.add(panelWithDraggabls);
		initAndAddPalette(dragController, mainContentPanel);
	}

	public void buildBoard(Dtos.WorkflowDto parentWorkflow,
			Dtos.WorkflowDto currentWorkflow, Dtos.ProjectDto project, FlexTable table,
			PickupDragController dragController, int row, int column) {
		if (currentWorkflow == null || currentWorkflow.getWorkflowitems().isEmpty()) {
			return;
		}

		Dtos.WorkflowitemDto current = currentWorkflow.getWorkflowitems().get(0);
		
		if (Dtos.ItemType.from(current.getItemType()) == Dtos.ItemType.HORIZONTAL) {
			table.setWidget(
					row,
					column,
					createDropTarget(dragController, currentWorkflow,
							current, Position.BEFORE,
							KanbanikResources.INSTANCE.rightDropArrowImage()));
			column++;
		} else if (Dtos.ItemType.from(current.getItemType()) == Dtos.ItemType.VERTICAL) {
			table.setWidget(
					row,
					column,
					createDropTarget(dragController, currentWorkflow,
							current, Position.BEFORE,
							KanbanikResources.INSTANCE.downDropArrowImage()));

			row++;
		} else {
			throw new IllegalStateException("Unsupported item type: '"
					+ current.getItemType() + "'");
		}

		for(Dtos.WorkflowitemDto currentItem : currentWorkflow.getWorkflowitems()) {
			if (!currentItem.getNestedWorkflow().getWorkflowitems().isEmpty()) {
				// this one has a child, so does not have a drop target in it's
				// body (content)
				FlexTable childTable = new FlexTable();
				childTable.setStyleName("boards-board");
				table.setWidget(
						row,
						column,
						createWorkflowitemPlace(dragController, currentItem,
								project, childTable));
				buildBoard(currentWorkflow, currentItem.getNestedWorkflow(), project,
						childTable, dragController, 0, 0);
			} else {
				// this one does not have a child yet, so create a drop target
				// so it can have in the future
				Panel dropTarget = createDropTarget(dragController,
						currentItem.getNestedWorkflow(), null, Position.INSIDE,
						KanbanikResources.INSTANCE.insideDropArrowImage());
				table.setWidget(
						row,
						column,
						createWorkflowitemPlace(dragController, currentItem,
								project, dropTarget));
			}

			if (Dtos.ItemType.from(currentItem.getItemType()) == Dtos.ItemType.HORIZONTAL) {
				column++;
				table.setWidget(
						row,
						column,
						createDropTarget(dragController, currentWorkflow,
								currentItem, Position.AFTER,
								KanbanikResources.INSTANCE.rightDropArrowImage()));
				column++;
			} else if (Dtos.ItemType.from(currentItem.getItemType()) == Dtos.ItemType.VERTICAL) {
				row++;
				table.setWidget(
						row,
						column,
						createDropTarget(dragController, currentWorkflow,
								currentItem, Position.AFTER,
								KanbanikResources.INSTANCE.downDropArrowImage()));
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}

		}

	}

	private Panel createDropTarget(PickupDragController dragController,
			Dtos.WorkflowDto contextItem, Dtos.WorkflowitemDto currentItem,
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
			Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Widget childTable) {

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
		registerListeners();
	}

	private void registerListeners() {
		if (!MessageBus.listens(BoardChangedMessage.class, this)) {
			MessageBus.registerListener(BoardChangedMessage.class, this);	
		}
	}

	public void deactivated() {
		unregisterListeners();
	}
	
	public void unregisterListeners() {
		MessageBus.unregisterListener(BoardChangedMessage.class, this);
	}

	public void messageArrived(Message<Dtos.BoardDto> message) {
		boardDto = message.getPayload();
		
		if (boardDto == null) {
			return;
		}
		
		boardDto = message.getPayload();
		renderBoard();
	}
	
}

