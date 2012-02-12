package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.shared.WorkflowDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class WorkflowItemEditComponent implements PanelContainingDialolgListener {
	
	private PanelToDTOConvertor convertor;

	private PanelContainingDialog dialog;
	
	private WorkflowItemEditPanel panel;
	
	private DraggableWorkflowItem item;
	
	private WorkflowDTO workflowDTO;
	
	private final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);
	
	public WorkflowItemEditComponent(WorkflowDTO workflowDTO, DraggableWorkflowItem item, PanelToDTOConvertor convertor) {
		super();
		this.convertor = convertor;
		this.item = item;
		this.workflowDTO = workflowDTO;
	}
	
	public void setHasClickHandlers(HasClickHandlers hasClickHandlers) {
		hasClickHandlers.addClickHandler(new ShowDialogHandler());		
	}
	
	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			show();
		}
		
	}

	public void okClicked(PanelContainingDialog dialog) {
		final WorkflowItemDTO dto = convertor.toDto(item.getDTO(), panel);
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						configureWorkflowService.storeWorkflowItem(workflowDTO, dto, new KanbanikAsyncCallback<WorkflowItemDTO>() {

							@Override
							public void success(WorkflowItemDTO result) {
								item.refreshDTO(result);
							}
						});				
					}
				}
		);
	}

	public void cancelClicked(PanelContainingDialog dialog) {
		
	}
	
	public void show() {
		panel = convertor.toPanel(item.getDTO());
		dialog = new PanelContainingDialog("Edit Workflow Item", panel);
		dialog.addListener(this);
		dialog.center();
	}
}
