package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.workflowitem.WorkflowitemChangedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

public class WorkflowitemWidget extends Composite implements HasDragHandle, MessageListener<Dtos.WorkflowitemDto>,ModulesLifecycleListener {
	
	@UiField
	PushButton editButton;
	
	@UiField
	PushButton deleteButton;
	
	@UiField
	Label workflowitemName;
	
	@UiField
	FocusPanel header;
	
	@UiField
	Panel content;

    @Override
    public void activated() {
        if (!MessageBus.listens(WorkflowitemChangedMessage.class, this)) {
            MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
        }
    }

    @Override
    public void deactivated() {
        MessageBus.unregisterListener(WorkflowitemChangedMessage.class, this);
    }

    interface MyUiBinder extends UiBinder<Widget, WorkflowitemWidget> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private Widget child;

	private Dtos.WorkflowitemDto workflowitem;
	
	public WorkflowitemWidget(Dtos.WorkflowitemDto workflowitem) {
		this.workflowitem = workflowitem;
		initWidget(uiBinder.createAndBindUi(this));
		refreshWorkflowitemName();
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		new WorkflowitemDeletingComponent(workflowitem, deleteButton);
		new WorkflowitemEditingComponent(workflowitem, editButton);

        new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		MessageBus.registerListener(WorkflowitemChangedMessage.class, this);
	}

	private void refreshWorkflowitemName() {
		workflowitemName.setText(createHeader(workflowitem));
	}

	private String createHeader(Dtos.WorkflowitemDto workflowitem) {
		String wip = workflowitem.getWipLimit() == -1 ? "" : " (" + workflowitem.getWipLimit() + ")";
		return workflowitem.getName() + wip;
	}
	
	public WorkflowitemWidget(Dtos.WorkflowitemDto workflowitem, Widget child) {
		this(workflowitem);
		this.child = child;
		content.add(child);
	}

	public FocusPanel getHeader() {
		return header;
	}

	public Widget getChild() {
		return child;
	}

	public Widget getDragHandle() {
		return header;
	}

	public Dtos.WorkflowitemDto getWorkflowitem() {
		return workflowitem;
	}

	public void messageArrived(Message<Dtos.WorkflowitemDto> message) {
		if (workflowitem.getId() != null && workflowitem.getId().equals(message.getPayload().getId())) {
			workflowitem = message.getPayload();
			refreshWorkflowitemName();
		}
	}
	
}
