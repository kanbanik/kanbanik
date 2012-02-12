package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class WorkflowitemMovingDropController extends HorizontalPanelDropController {

	private WorkflowDTO workflowDTO;
	
	private WorkflowPanel target;
	
	private Widget positioner;
	
	private final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);
	
	public WorkflowitemMovingDropController(WorkflowPanel dropTarget, WorkflowDTO workflowDTO) {
		super(dropTarget);
		this.workflowDTO = workflowDTO;
		this.target = dropTarget;
	}

	@Override
	protected Widget newPositioner(DragContext context) {
		positioner = super.newPositioner(context);
		return positioner;
	}
	
	@Override
	public void onDrop(DragContext context) {
		
		super.onDrop(context);
		
		if (context.selectedWidgets == null || context.selectedWidgets.size() != 1) {
			return;
		}
		
		DraggableWorkflowItem item = null;
		if (context.selectedWidgets.get(0) instanceof DraggableWorkflowItem) {
			item = (DraggableWorkflowItem) context.selectedWidgets.get(0);
		} else {
			return;
		}
		
		int order = findOrder();
		
		if (order == -1) {
			// not found, strange, ignore for now
			// TODO handle this
			return;
		}
		if (order == 2) {
			// 2 because: 
			// 0 -> empty label (has to be there)
			// 1 -> the widget itself
			// 2 -> the positioner of the widget
			addToBeginning(item);
		} else if (order == target.getWidgetCount() -1) {
			addToEnd(item);
		} else {
			addToMiddle(item, order);
		}
		
		storeOnServer(item);
	}

	private void storeOnServer(final DraggableWorkflowItem item) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.storeWorkflowItem(workflowDTO, item.getDTO(), new KanbanikAsyncCallback<WorkflowItemDTO>() {

							@Override
							public void success(WorkflowItemDTO result) {
								item.refreshDTO(result);
							}
						});				
					}
				}
		);
	}

	private void addToEnd(DraggableWorkflowItem item) {
		item.setNextItemsId(-1);
	}

	private void addToMiddle(DraggableWorkflowItem item, int order) {
		int nextItemOrder = order + 1;
		item.setNextItemsId(getIdOfItem(nextItemOrder));
	}

	private void addToBeginning(DraggableWorkflowItem item) {
		if (target.getWidgetCount() <= 3) {
			// it is the end because no other item follows
			addToEnd(item);
			return;
		}
		
		int nextItemOrder = 3;
		item.setNextItemsId(getIdOfItem(nextItemOrder));
	}

	private int getIdOfItem(int index) {
		Widget item = target.getWidget(index);
		if (item instanceof DraggableWorkflowItem) {
			return ((DraggableWorkflowItem)item).getId();
		}
		
		return -1;
	}


	private int findOrder() {
		return target.getWidgetIndex(positioner);
	}
}
