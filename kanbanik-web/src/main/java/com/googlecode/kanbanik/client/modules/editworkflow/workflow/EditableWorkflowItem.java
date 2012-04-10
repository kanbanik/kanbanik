package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;

public class EditableWorkflowItem extends Composite implements DraggableWorkflowItem {
	
	@UiField(provided=true)
	Panel item;
	
	@UiField
	PushButton editButton;
	
	@UiField
	PushButton deleteButton;
	
	private WorkflowItemEditComponent editComponent;
	
	interface MyUiBinder extends UiBinder<Widget, EditableWorkflowItem> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final WorkflowitemDeletingComponent deleteComponent;
	
	public EditableWorkflowItem(DraggableWorkflowItem item, WorkflowItemEditComponent editComponent, WorkflowitemDeletingComponent deleteComponent) {
		super();
		
		if (!(item instanceof DraggableWorkflowItem)) {
			throw new IllegalArgumentException("The item has to be of type DraggableWorkflowItem");
		}
		
		this.item = (Panel) item;
		this.editComponent = editComponent;
		this.deleteComponent = deleteComponent;
		
		initWidget(uiBinder.createAndBindUi(this));
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		editComponent.setHasClickHandlers(editButton);
		deleteComponent.setHasClickHandlers(deleteButton);
	}
	
	public Widget cloneItem() {
		return new EditableWorkflowItem((DraggableWorkflowItem)((DraggableWorkflowItem)item).cloneItem(), editComponent, deleteComponent);
	}
	
	public FocusPanel getHeader() {
		return (FocusPanel) item;
	}

	public void setNextItemsId(int id) {
		((DraggableWorkflowItem)item).setNextItemsId(id);
	}

	public int getId() {
		return ((DraggableWorkflowItem)item).getId();
	}

	public void setId(int id) {
		((DraggableWorkflowItem)item).setId(id);
	}

	public WorkflowItemDTO getDTO() {
		return ((DraggableWorkflowItem)item).getDTO();
	}

	public void refreshDTO(WorkflowItemDTO dto) {
		((DraggableWorkflowItem)item).refreshDTO(dto);
	}
}
