package com.googlecode.kanbanik.client.components.task;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ResourceClosingAsyncCallback;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.common.KanbanikRichTextArea;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.dto.ClassOfService;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public abstract class AbstractTaskEditingComponent {
	
	private Panel panel = new VerticalPanel();
	
	private TextBox taskName = new TextBox();
	
	private Label ticketId = new Label("");
	
	private KanbanikRichTextArea description;
	
	private ListBox classOfService = new ListBox();
	
	private PanelContainingDialog dialog;
	
	private String name;

	private final HasClickHandlers clickHandler;
	
	public AbstractTaskEditingComponent(HasClickHandlers clickHandler) {
		this.clickHandler = clickHandler;
		this.name = "Task Details";
	}
	
	protected void initialize() {
		description = new KanbanikRichTextArea();
		
		Grid header = new Grid(3, 2);
		header.setWidget(0, 0, new Label("ID"));
		header.setWidget(0, 1, ticketId);
		
		taskName.setWidth("100%");
		header.setWidget(1, 0, new Label("Short description"));
		header.setWidget(1, 1, taskName);
		
		header.setWidget(2, 0, new Label("Class of service"));
		header.setWidget(2, 1, classOfService);
		
		header.setWidth("640px");
		
		panel.add(header);
		panel.add(description);
		panel.setWidth("100%");
		
		setupValues();
		
		dialog = new PanelContainingDialog(name, panel, taskName);
		dialog.addListener(new AddTaskButtonHandler());
		clickHandler.addClickHandler(new ShowDialogHandler());
	}
	
	private void setupValues() {
		ticketId.setText(getTicketId());
		taskName.setValue(getTaskName());
		description.setHtml(getDescription());
		
		String currentClassOfService = getClassOfServiceAsString();
		classOfService.clear();

		int selectedIndex = 0;
		int i = 0;
		for(ClassOfService item : ClassOfService.values()) {
			classOfService.addItem(item.toString());
			if (item.toString().equals(currentClassOfService)) {
				selectedIndex = i;
			}
			
			i ++;
		}
		classOfService.setSelectedIndex(selectedIndex);
	}

	protected abstract String getClassOfServiceAsString();
	protected abstract String getTicketId();
	protected abstract String getTaskName();
	protected abstract String getDescription();
	protected abstract String getId();
	protected abstract int getVersion();

	private TaskDto createTaskDTO() {
		TaskDto taskDto = createBasicDTO();
		taskDto.setName(taskName.getText());
		taskDto.setDescription(description.getHtml());
		taskDto.setClassOfService(ClassOfService.STANDARD);
		taskDto.setId(getId());
		taskDto.setClassOfService(getClassOfService());
		taskDto.setVersion(getVersion());
		return taskDto;
	}

	protected abstract TaskDto createBasicDTO();

	private ClassOfService getClassOfService() {
		int index = classOfService.getSelectedIndex();
		String value = classOfService.getValue(index);
		for(ClassOfService item : ClassOfService.values()) {
			if (item.toString().equals(value)) {
				return item;
			}
		}
		
		return ClassOfService.STANDARD;
	}

	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			onClicked();
		}
		
	}
	
	protected void onClicked() {
		doSetupAndShow();
	}

	protected void doSetupAndShow() {
		setupValues();
		dialog.center();
		taskName.setFocus(true);
	}
	
	class AddTaskButtonHandler implements PanelContainingDialolgListener {

		public void okClicked(final PanelContainingDialog dialog) {
			
			final TaskDto taskDto = createTaskDTO();
			
			new KanbanikServerCaller(
					new Runnable() {

						public void run() {
							final boolean isNew = taskDto.getId() == null;
							ServerCommandInvokerManager.getInvoker().<SimpleParams<TaskDto>, FailableResult<SimpleParams<TaskDto>>> invokeCommand(
									ServerCommand.SAVE_TASK,
									new SimpleParams<TaskDto>(taskDto),
									new ResourceClosingAsyncCallback<FailableResult<SimpleParams<TaskDto>>>(dialog) {

										@Override
										public void success(FailableResult<SimpleParams<TaskDto>> result) {
											if (isNew) {
												MessageBus.sendMessage(new TaskAddedMessage(result.getPayload().getPayload(), AbstractTaskEditingComponent.this));
											} else {
												MessageBus.sendMessage(new TaskEditedMessage(result.getPayload().getPayload(), AbstractTaskEditingComponent.this));
											}
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
