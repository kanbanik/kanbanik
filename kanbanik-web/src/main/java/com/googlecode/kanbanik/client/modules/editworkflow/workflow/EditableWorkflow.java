package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.model.WorkflowItemGUI;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.QueuedItemDTO;
import com.googlecode.kanbanik.shared.RegularItemDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;

public class EditableWorkflow extends VerticalPanel implements MessageListener<WorkflowItemDTO>, ModulesLifecycleListener {
	
	private BoardDTO board;

	private WorkflowDTO workflowDTO;
	
	private WorkflowItemPalette workflowItemPalette;

	private AbsolutePanel panelWithDraggablePanels = new AbsolutePanel();

	private WorkflowDragController dragController = new WorkflowDragController(panelWithDraggablePanels, false);
	
	private WorkflowPanel workflow;
	
	public EditableWorkflow(BoardDTO board) {
		super();
		this.board = board;
		this.workflowDTO = board.getWorkflow();
		init();
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		MessageBus.registerListener(WorkflowitemDeletedMessage.class, this);
	}

	private void init() {
		workflow = new WorkflowPanel(dragController, new EditableWorkflowItemBuilder(workflowDTO));
		workflowItemPalette = new WorkflowItemPalette(dragController);
		
		workflow.setStyleName("edit-workflow-workflow-holder");
		workflow.add(new Label(""));
		panelWithDraggablePanels.add(workflow);
		HorizontalPanelDropController dropController = new WorkflowitemMovingDropController(workflow, workflowDTO);
		dragController.registerDropController(dropController);
		
		initPalette();
		initWorkflow();
		
		panelWithDraggablePanels.setStyleName("panel-with-draggable-panels-style");
		panelWithDraggablePanels.add(new Label("Drag and drop items from palette"));
		panelWithDraggablePanels.add(workflowItemPalette);
		add(panelWithDraggablePanels);
	}

	private void initWorkflow() {
		for (WorkflowItemDTO item : board.getWorkflow().getWorkflowItems()) {
			if (item instanceof RegularItemDTO) {
				workflow.add(new DraggableRegularWorkflowItem(item, new WorkflowItemGUI(new WorkflowItemPlaceDTO(-1))));
			} else if (item instanceof QueuedItemDTO) {
				workflow.add(new DraggableQueuedItem(item, new WorkflowItemGUI(new WorkflowItemPlaceDTO(-1)), new WorkflowItemGUI(new WorkflowItemPlaceDTO(-1))));
			}
		}
	}

	private void initPalette() {
		RegularItemDTO regularItemDTO = new RegularItemDTO();
		regularItemDTO.setId(-1);
		regularItemDTO.setWipLimit(1);
		regularItemDTO.setName("Example Name");
		workflowItemPalette.addWithDraggable(new DraggableRegularWorkflowItem(regularItemDTO, new WorkflowItemGUI(new WorkflowItemPlaceDTO(-1))));
		
		QueuedItemDTO queuedItemDTO = new QueuedItemDTO();
		queuedItemDTO.setId(-1);
		queuedItemDTO.setWipLimit(1);
		queuedItemDTO.setName("Example Name");
		workflowItemPalette.addWithDraggable(new DraggableQueuedItem(queuedItemDTO, new WorkflowItemGUI(new WorkflowItemPlaceDTO(-1)), new WorkflowItemGUI(new WorkflowItemPlaceDTO(-1))));
	}		

	public void messageArrived(Message<WorkflowItemDTO> message) {
		WorkflowItemDTO deletedDto = message.getPayload();
		int indexToDelete = -1;
		for (int i = 0; i < workflow.getWidgetCount(); i++) {
			if (!(workflow.getWidget(i) instanceof DraggableWorkflowItem)) {
				continue;
			}
			
			DraggableWorkflowItem item = (DraggableWorkflowItem) workflow.getWidget(i);
			if (item.getDTO().getId() != deletedDto.getId()) {
				continue;
			}
			
			indexToDelete = i;
			break;
		}
		
		if (indexToDelete != -1) {
			workflow.remove(indexToDelete);
		}
	}

	public void activated() {
		if (!MessageBus.listens(WorkflowitemDeletedMessage.class, this)) {
			MessageBus.registerListener(WorkflowitemDeletedMessage.class, this);	
		}
	}

	public void deactivated() {
		MessageBus.unregisterListener(WorkflowitemDeletedMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
	}
}