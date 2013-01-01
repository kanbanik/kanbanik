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
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardRefreshedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardsRefreshRequestMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ItemType;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class WorkflowEditingComponent extends Composite implements
		ModulesLifecycleListener, MessageListener<BoardDto> {

	interface MyUiBinder extends UiBinder<Widget, WorkflowEditingComponent> {
	}
	
	public interface Style extends CssResource {
        String bordered();
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

	private BoardDto boardDto;

	private BoardsRefreshRequestMessageListener boardsRefreshRequestMessageListener;
	
	public WorkflowEditingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}

	public void initialize(BoardWithProjectsDto boardWithProjects) {
		this.boardDto = boardWithProjects.getBoard();
		renderBoard();
		
		registerListeners();
	}
	
	private void initAndAddPalette(PickupDragController dragController, FlowPanel mainContentPanel) {
		if (boardDto == null) {
			return;
		}
		
		WorkflowitemDto horizontal = new WorkflowitemDto();
		horizontal.setParentWorkflow(boardDto.getWorkflow());
		horizontal.setNestedWorkflow(createNestedWorkflow());
		horizontal.setItemType(ItemType.HORIZONTAL);
		horizontal.setName("Horizontal Item");
		horizontal.setWipLimit(-1);
		
		WorkflowitemDto vertical = new WorkflowitemDto();
		vertical.setParentWorkflow(boardDto.getWorkflow());
		vertical.setNestedWorkflow(createNestedWorkflow());
		vertical.setItemType(ItemType.VERTICAL);
		vertical.setName("Vertical Item");
		vertical.setWipLimit(-1);
		
		
		WorkflowItemPalette paletteContent = new WorkflowItemPalette(dragController);
		paletteContent.addWithDraggable(new PaletteWorkflowitemWidget(horizontal, imageResourceAsPanel(KanbanikResources.INSTANCE.rightDropArrowImage())));
		paletteContent.addWithDraggable(new PaletteWorkflowitemWidget(vertical, imageResourceAsPanel(KanbanikResources.INSTANCE.downDropArrowImage())));
		FlowPanel designPanel = new FlowPanel();
		
		Label headerLabel = new Label("Workflowitem Palette");
		headerLabel.setStyleName(style.headerTextStyle());
		
		Label descriptionLabel = new Label("Drag and drop workflowitems from palette to workflow");
		
		designPanel.add(headerLabel);
		designPanel.add(descriptionLabel);
		designPanel.add(paletteContent);
		designPanel.setStyleName(style.palettePanelStyle());
		
		mainContentPanel.add(designPanel);
	}

	private WorkflowDto createNestedWorkflow() {
		WorkflowDto dto = new WorkflowDto();
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
		PickupDragController dragController = new PickupDragController(
				panelWithDraggabls, false);
		
		mainContentPanel.add(tableDesignPanel);
		panelWithDraggabls.add(mainContentPanel);

		buildBoard(null, boardDto.getWorkflow(), null, table,
				dragController, 0, 0);

		// default DTO
		if (boardDto.getWorkflow().getWorkflowitems().size() == 0) {
			table.setWidget(
					0,
					0,
					createDropTarget(dragController, 
							createNestedWorkflow(),
							null, 
							Position.BEFORE,
							KanbanikResources.INSTANCE.insideDropArrowImage()));
		}
		
		board.add(panelWithDraggabls);
		initAndAddPalette(dragController, mainContentPanel);
	}

	public void buildBoard(WorkflowDto parentWorkflow,
			WorkflowDto currentWorkflow, ProjectDto project, FlexTable table,
			PickupDragController dragController, int row, int column) {
		if (currentWorkflow == null || currentWorkflow.getWorkflowitems().size() == 0) {
			return;
		}

		WorkflowitemDto current = currentWorkflow.getWorkflowitems().get(0);
		
		if (current.getItemType() == ItemType.HORIZONTAL) {
			table.setWidget(
					row,
					column,
					createDropTarget(dragController, currentWorkflow,
							current, Position.BEFORE,
							KanbanikResources.INSTANCE.rightDropArrowImage()));
			column++;
		} else if (current.getItemType() == ItemType.VERTICAL) {
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

		for(WorkflowitemDto currentItem : currentWorkflow.getWorkflowitems()) {
			if (currentItem.getNestedWorkflow().getWorkflowitems().size() != 0) {
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

			if (currentItem.getItemType() == ItemType.HORIZONTAL) {
				column++;
				table.setWidget(
						row,
						column,
						createDropTarget(dragController, currentWorkflow,
								currentItem, Position.AFTER, KanbanikResources.INSTANCE.rightDropArrowImage()));
				column++;
			} else if (currentItem.getItemType() == ItemType.VERTICAL) {
				row++;
				table.setWidget(
						row,
						column,
						createDropTarget(dragController, currentWorkflow,
								currentItem, Position.AFTER, KanbanikResources.INSTANCE.downDropArrowImage()));
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}

		}

	}

	private Panel createDropTarget(PickupDragController dragController,
			WorkflowDto contextItem, WorkflowitemDto currentItem,
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
		registerListeners();
	}

	private void registerListeners() {
		boardsRefreshRequestMessageListener = new BoardsRefreshRequestMessageListener();
		MessageBus.registerListener(BoardsRefreshRequestMessage.class, boardsRefreshRequestMessageListener);
		
		if (!MessageBus.listens(BoardChangedMessage.class, this)) {
			MessageBus.registerListener(BoardChangedMessage.class, this);	
		}
	}

	public void deactivated() {
		unregisterListeners();
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
	
	public void unregisterListeners() {
		if (boardsRefreshRequestMessageListener != null) {
			MessageBus.unregisterListener(BoardsRefreshRequestMessage.class, boardsRefreshRequestMessageListener);
		}
		
		MessageBus.unregisterListener(BoardChangedMessage.class, this);
	}

	public void messageArrived(Message<BoardDto> message) {
		boardDto = message.getPayload();
		
		if (boardDto == null) {
			return;
		}
		
		refreshBoards();
	}
	
	private void refreshBoards() {
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
						new BaseAsyncCallback<SimpleParams<BoardDto>>() {

							@Override
							public void success(SimpleParams<BoardDto> result) {
								boardDto = result.getPayload();
								if (boardDto != null) {
									// it has been deleted
									MessageBus.sendMessage(new BoardRefreshedMessage(boardDto, WorkflowEditingComponent.this));
									// can not sent a refresh request - it would reload the whole again
									renderBoard();
								}
							}

						});
		}});
	}

	class BoardsRefreshRequestMessageListener implements MessageListener<String> {

		public void messageArrived(Message<String> message) {
			refreshBoards();
		}
		
	}
}

