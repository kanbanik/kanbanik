package com.googlecode.kanbanik.client.components.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
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

	private SuggestBox assigneeEditor;

	private SuggestBox classOfServiceEditor;

	private CheckBox dueDateCheckBox = new CheckBox();
	
	private TextBox dueDateTextBox = new TextBox();
	
	private DatePicker dueDatePicker = new DatePicker();

	private PanelContainingDialog dialog;

	private String name;

	private final HasClickHandlers clickHandler;

	public AbstractTaskEditingComponent(HasClickHandlers clickHandler) {
		this.clickHandler = clickHandler;
		this.name = "Task Details";
	}

	protected void initialize() {
		// just testing data - will be replaced by data from the DTO
		List<String> classOfServiceStrings = Arrays.asList(
				"some class of service",
				"other class of service",
				"hmm class of service",
				"some class of service1"
		);
		
		classOfServiceEditor = new SuggestBox(createOracle(classOfServiceStrings));
		initSuggestBox(classOfServiceEditor);
		
		
		List<String> assigneeStrings = Arrays.asList(
			"jim",
			"joe",
			"ignac",
			"frikulin",
			"bfu",
			"brindzonos"
		);
		assigneeEditor = new SuggestBox(createOracle(assigneeStrings));
		initSuggestBox(assigneeEditor);
		
		description = new KanbanikRichTextArea();
		
		Grid header = new Grid(5, 2);
		header.setWidget(0, 0, new Label("ID"));
		header.setWidget(0, 1, ticketId);
		
		taskName.setWidth("100%");
		header.setWidget(1, 0, new Label("Short Description"));
		header.setWidget(1, 1, taskName);
		
		header.setWidget(2, 0, new Label("Class of service"));
		header.setWidget(2, 1, classOfServiceEditor);
		
		header.setWidget(3, 0, new Label("Assignee"));
		header.setWidget(3, 1, assigneeEditor);
		
		header.setWidget(4, 0, new Label("Due Date"));
		
		header.setWidget(4, 1, createDueDatePanel());
		
		header.setWidth("640px");
		
		panel.add(header);
		panel.add(description);
		panel.setWidth("100%");
		
		setupValues();
		
		dialog = new PanelContainingDialog(name, panel, taskName);
		dialog.addListener(new AddTaskButtonHandler());
		clickHandler.addClickHandler(new ShowDialogHandler());
	}
	
	class DatePickerDialog extends DialogBox {
		public DatePickerDialog() {
			setText("Due Date");
			setWidget(dueDatePicker);
			dueDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			      public void onValueChange(ValueChangeEvent<Date> event) {
			          Date date = event.getValue();
			          String dateString = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(date);
			          dueDateTextBox.setText(dateString);
			          hide();
			          
			        }
			      });
		}
		
		@Override
		 public boolean onKeyDownPreview(char key, int modifiers) {
		     switch (key) {
		       case KeyCodes.KEY_ESCAPE:
		         hide();
		         break;
		     }

		     return true;
		 }
		
		@Override
		public void hide() {
			super.hide();
			dueDateTextBox.setFocus(true);
		}
		
		@Override
		public void show() {
			setPopupPosition(dueDateTextBox.getAbsoluteLeft(), dueDateTextBox.getAbsoluteTop() + 30);
			super.show();
		}
	}
	
	private Panel createDueDatePanel() {
		
		final DialogBox datePickerDialog = new DatePickerDialog();
		
		Panel dueDatePanel = new VerticalPanel();
		dueDatePanel.add(dueDateCheckBox);
		dueDatePanel.add(dueDateTextBox);
		dueDateCheckBox.setValue(false);
		dueDateCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dueDateTextBox.setVisible(dueDateCheckBox.getValue());
			}
		});
		dueDateTextBox.setVisible(false);
		
		dueDateTextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				datePickerDialog.show();
			}
		});
		return dueDatePanel;
	}

	private MultiWordSuggestOracle createOracle(List<String> suggestions) {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.addAll(suggestions);

		List<Suggestion> defaults = new ArrayList<Suggestion>();
		for (String suggestion : suggestions) {
			defaults.add(new SimpleSuggestion(suggestion));
		}
		oracle.setDefaultSuggestions(defaults);

		return oracle;
	}

	private void initSuggestBox(final SuggestBox suggestBox) {
		suggestBox.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				suggestBox.showSuggestionList();
			}
		});
	}

	private void setupValues() {
		ticketId.setText(getTicketId());
		taskName.setValue(getTaskName());
		description.setHtml(getDescription());

		String currentClassOfService = getClassOfServiceAsString();
		classOfService.clear();

		int selectedIndex = 0;
		int i = 0;
		for (ClassOfService item : ClassOfService.values()) {
			classOfService.addItem(item.toString());
			if (item.toString().equals(currentClassOfService)) {
				selectedIndex = i;
			}

			i++;
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
//		taskDto.setClassOfService(ClassOfService.STANDARD);
		taskDto.setId(getId());
//		taskDto.setClassOfService(getClassOfService());
		taskDto.setVersion(getVersion());
		return taskDto;
	}

	protected abstract TaskDto createBasicDTO();

	private ClassOfService getClassOfService() {
		int index = classOfService.getSelectedIndex();
		String value = classOfService.getValue(index);
		for (ClassOfService item : ClassOfService.values()) {
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

			new KanbanikServerCaller(new Runnable() {

				public void run() {
					final boolean isNew = taskDto.getId() == null;
					ServerCommandInvokerManager
							.getInvoker()
							.<SimpleParams<TaskDto>, FailableResult<SimpleParams<TaskDto>>> invokeCommand(
									ServerCommand.SAVE_TASK,
									new SimpleParams<TaskDto>(taskDto),
									new ResourceClosingAsyncCallback<FailableResult<SimpleParams<TaskDto>>>(
											dialog) {

										@Override
										public void success(
												FailableResult<SimpleParams<TaskDto>> result) {
											if (isNew) {
												MessageBus
														.sendMessage(new TaskAddedMessage(
																result.getPayload()
																		.getPayload(),
																AbstractTaskEditingComponent.this));
											} else {
												MessageBus
														.sendMessage(new TaskEditedMessage(
																result.getPayload()
																		.getPayload(),
																AbstractTaskEditingComponent.this));
											}
										}
									});

				}
			});

		}

		public void cancelClicked(PanelContainingDialog dialog) {

		}

	}

	class SimpleSuggestion implements Suggestion {

		private String str;

		public SimpleSuggestion(String str) {
			this.str = str;
		}

		@Override
		public String getDisplayString() {
			return str;
		}

		@Override
		public String getReplacementString() {
			return str;
		}

	}
}
