package com.googlecode.kanbanik.client.components.task;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.shared.ClassOfServiceDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public abstract class AbstractTaskEditingComponent {
	
	private Panel panel = new VerticalPanel();
	
	private TextBox taskName = new TextBox();
	
	private Label ticketId = new Label("");
	
	private Widget description;
	
	private RichTextArea richTextArea;
	
	private ListBox classOfService = new ListBox();
	
	private PanelContainingDialog dialog;
	
	private String name;

	private final HasClickHandlers clickHandler;
	
	public AbstractTaskEditingComponent(HasClickHandlers clickHandler) {
		this.clickHandler = clickHandler;
		this.name = "Task Details";
	}
	
	protected void initialize() {
		description = initializeRichTextEditor();
		
		Grid header = new Grid(3, 2);
		header.setWidget(0, 0, new Label("ID"));
		header.setWidget(0, 1, ticketId);
		
		header.setWidget(1, 0, new Label("Short descritption"));
		header.setWidget(1, 1, taskName);
		
		header.setWidget(2, 0, new Label("Class of service"));
		header.setWidget(2, 1, classOfService);
		
		header.setWidth("500px");
		
		panel.add(header);
		panel.add(description);
		panel.setWidth("100%");
		
		setupValues();
		
		dialog = new PanelContainingDialog(name, panel);
		dialog.addListener(new AddTaskButtonHandler());
		clickHandler.addClickHandler(new ShowDialogHandler());
	}
	
	  public Widget initializeRichTextEditor() {
		    richTextArea = new RichTextArea();
		    richTextArea.ensureDebugId("cwRichText-area");
		    richTextArea.setSize("100%", "600px");
		    RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
		    toolbar.ensureDebugId("cwRichText-toolbar");
		    toolbar.setWidth("100%");

		    Grid grid = new Grid(2, 1);
		    grid.setStyleName("cw-RichText");
		    grid.setWidget(0, 0, toolbar);
		    grid.setWidget(1, 0, richTextArea);
		    return grid;
		  }
	
	private void setupValues() {
		ticketId.setText(getTicketId());
		taskName.setValue(getTaskName());
		richTextArea.setHTML(getDescription());
		
		String currentClassOfService = getClassOfService();
		classOfService.clear();
		classOfService.addItem(currentClassOfService);
		for(ClassOfServiceDTO item : ClassOfServiceDTO.values()) {
			if (item.toString().equals(currentClassOfService)) {
				continue;
			}
			classOfService.addItem(item.toString());
		}
	}

	protected abstract String getClassOfService();
	protected abstract String getTicketId();
	protected abstract String getTaskName();
	protected abstract String getDescription();
	protected abstract int getId();

	private TaskDTO createTaskDTO() {
		TaskDTO taskDTO = createBasicDTO();
		taskDTO.setName(taskName.getText());
		taskDTO.setDescription(richTextArea.getHTML());
		taskDTO.setClassOfService(getClassOfServiceDTO());
		taskDTO.setId(getId());
		return taskDTO;
	}

	protected abstract TaskDTO createBasicDTO();

	private ClassOfServiceDTO getClassOfServiceDTO() {
		int index = classOfService.getSelectedIndex();
		String value = classOfService.getValue(index);
		for(ClassOfServiceDTO item : ClassOfServiceDTO.values()) {
			if (item.toString().equals(value)) {
				return item;
			}
		}
		
		return ClassOfServiceDTO.STANDARD;
	}

	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			setupValues();
			dialog.center();
			taskName.setFocus(true);
		}
		
	}
	
	class AddTaskButtonHandler implements PanelContainingDialolgListener {

		public void okClicked(PanelContainingDialog dialog) {
			MessageBus.sendMessage(new TaskChangedMessage(createTaskDTO(), AbstractTaskEditingComponent.this));
		}

		public void cancelClicked(PanelContainingDialog dialog) {
			
		}
		
	}
}
