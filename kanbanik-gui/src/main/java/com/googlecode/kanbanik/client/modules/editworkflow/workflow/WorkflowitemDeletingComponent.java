package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowService;
import com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;
import com.googlecode.kanbanik.shared.WorkflowDTO;

public class WorkflowitemDeletingComponent implements ClickHandler {

	private final ConfigureWorkflowServiceAsync configureWorkflowService = GWT.create(ConfigureWorkflowService.class);

	private WorkflowDTO workflowDTO;

	private PanelContainingDialog yesNoDialog;

	private HorizontalPanel warningPanel = new HorizontalPanel();
	
	private DraggableWorkflowItem item;
	
	public WorkflowitemDeletingComponent(WorkflowDTO workflowDTO, DraggableWorkflowItem item) {
		this.workflowDTO = workflowDTO;
		this.item = item;
		warningPanel.add(new Label("Are you sure to delete this workflowitem '" + item.getDTO().getName() + "' ?"));
	}

	public void setHasClickHandlers(HasClickHandlers hasClickHandlers) {
		hasClickHandlers.addClickHandler(this);
	}

	public void onClick(ClickEvent event) {
			if (item.getId() == -1) {
				return;
			}
			
			yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
			yesNoDialog.addListener(new YesNoDialogListener(item));
			yesNoDialog.center();
	}
	
	class YesNoDialogListener implements PanelContainingDialolgListener {
		
		private DraggableWorkflowItem item;
		
		public YesNoDialogListener(DraggableWorkflowItem item) {
			this.item = item;
		}

		public void okClicked(PanelContainingDialog dialog) {
			new KanbanikServerCaller(
					new Runnable() {

						public void run() {
							configureWorkflowService.deleteWorkflowItem(workflowDTO, item.getDTO(), new KanbanikAsyncCallback<ReturnObjectDTO>() {

								@Override
								public void success(ReturnObjectDTO result) {
									if (!result.isOK()) {
										new ErrorDialog(result.getMessage()).center();
									} else {
										MessageBus.sendMessage(new WorkflowitemDeletedMessage(item.getDTO(), this));
									}
								}
								
								public void onFailure(Throwable caught) {
									super.onFailure(caught);
								}

							});				
						}
					}
					);
		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}
	}

}
