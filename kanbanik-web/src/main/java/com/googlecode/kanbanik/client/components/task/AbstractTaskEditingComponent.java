package com.googlecode.kanbanik.client.components.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
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
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;
import com.googlecode.kanbanik.dto.TaskDto;
import com.googlecode.kanbanik.dto.UserDto;
import com.googlecode.kanbanik.dto.shell.FailableResult;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.shared.ServerCommand;

// TODO it start to look really ugly - needs to be cleaned up into ui.xml
public abstract class AbstractTaskEditingComponent {

	private Panel panel = new VerticalPanel();

	private TextBox taskName = new TextBox();

	private Label ticketId = new Label("");

	private KanbanikRichTextArea description;

	private SuggestBox assigneeEditor;

	private SuggestBox classOfServiceEditor;

	private CheckBox dueDateCheckBox = new CheckBox();
	
	private TextBox dueDateTextBox = new TextBox();
	
	private DatePicker dueDatePicker = new DatePicker();

	private PanelContainingDialog dialog;
	
	private HTML warningMessages = new HTML();

	private String name;

	private final HasClickHandlers clickHandler;

	private Map<String, ClassOfServiceDto> classOfServiceToName;
	
	private Map<String, UserDto> userToName;

	private BoardDto boardDto;

	public AbstractTaskEditingComponent(HasClickHandlers clickHandler, BoardDto boardDto) {
		this.clickHandler = clickHandler;
		this.boardDto = boardDto;
		this.name = "Task Details";
	}

	protected void initialize() {
		
		dialog = new PanelContainingDialog(name, panel, taskName);
		
		classOfServiceEditor = new PanelContainingDialogSuggestBox(new MultiWordSuggestOracle(), dialog);
		initSuggestBox(classOfServiceEditor);
		
		assigneeEditor = new PanelContainingDialogSuggestBox(new MultiWordSuggestOracle(), dialog);
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
		
		warningMessages.getElement().getStyle().setColor("red");
		
		panel.add(header);
		panel.add(description);
		panel.add(warningMessages);
		panel.setWidth("100%");
		
		setupValues();
		
		
		dialog.addListener(new AddTaskButtonHandler());
		clickHandler.addClickHandler(new ShowDialogHandler());
	}
	
	private Map<String, ClassOfServiceDto> initClassOfServiceToName(List<ClassOfServiceDto> classesOfService) {
		
		Map<String, ClassOfServiceDto> res = new HashMap<String, ClassOfServiceDto>();
		for (ClassOfServiceDto classOfService : classesOfService) {
			res.put(classOfService.getName(), classOfService);
		}
		
		return res;
	}

	private Map<String, UserDto> initUserToName(List<UserDto> users) {
		Map<String, UserDto> res = new HashMap<String, UserDto>();
		for (UserDto user : users) {
			res.put(user.getUserName(), user);
		}
		
		return res;
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

	private void fillOracle(Set<String> suggestions, MultiWordSuggestOracle oracle) {
		oracle.addAll(suggestions);

		List<Suggestion> defaults = new ArrayList<Suggestion>();
		for (String suggestion : suggestions) {
			defaults.add(new SimpleSuggestion(suggestion));
		}
		oracle.setDefaultSuggestions(defaults);

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

		
		classOfServiceToName = initClassOfServiceToName(ClassOfServicesManager.getInstance().getForBoard(boardDto));
		fillOracle(classOfServiceToName.keySet(), (MultiWordSuggestOracle) classOfServiceEditor.getSuggestOracle());
		classOfServiceEditor.setValue(getClassOfServiceAsString());
		
		userToName = initUserToName(UsersManager.getInstance().getUsers());
		fillOracle(userToName.keySet(), (MultiWordSuggestOracle) assigneeEditor.getSuggestOracle());
		assigneeEditor.setValue(getUser());
		
		boolean dueDateSet = getDueDate() != null && !"".equals(getDueDate());
		dueDateCheckBox.setValue(dueDateSet);
		dueDateTextBox.setVisible(dueDateSet);
		dueDateTextBox.setText(getDueDate());
	}

	private boolean validate() {
		boolean dueDateValid = validateDueDate();
		boolean classOfServiceValid = validateClassOfService();
		boolean assigneeValid = validateAssignee();
		return dueDateValid && classOfServiceValid && assigneeValid;
	}
	
	private boolean validateClassOfService() {
		if (classOfServiceToName.containsKey(classOfServiceEditor.getText().trim())) {
			return true;
		}
		
		addWarningMessage("The given class of service is not defined");
		return false;
	}
	
	private boolean validateAssignee() {
		if (assigneeEditor.getText() == null || "".equals(assigneeEditor.getText())) {
			// empty is OK
			return true;
		}

		if (userToName.containsKey(assigneeEditor.getText().trim())) {
			return true;
		}
		
		addWarningMessage("The given assignee is not an existing user");
		return false;
	}

	private boolean validateDueDate() {
		if (!dueDateCheckBox.getValue()) {
			return true;
		}
		
		String dueDateString = dueDateTextBox.getValue();
		try {
			DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).parse(dueDateString);
			return true;
		} catch(IllegalArgumentException e) {
			addWarningMessage("Accepted date format is YYYY-MM-DD");
			return false;
		}
	
	}
	
	private void addWarningMessage(String msg) {
		warningMessages.setHTML(SafeHtmlUtils.fromTrustedString(warningMessages.getHTML() + msg + "</br>"));
	}

	protected abstract String getClassOfServiceAsString();
	
	protected abstract String getUser();

	protected abstract String getTicketId();

	protected abstract String getTaskName();

	protected abstract String getDescription();

	protected abstract String getId();

	protected abstract int getVersion();

	protected abstract TaskDto createBasicDTO();

	private TaskDto createTaskDTO() {
		TaskDto taskDto = createBasicDTO();
		taskDto.setName(taskName.getText());
		taskDto.setDescription(description.getHtml());
		ClassOfServiceDto selectedClassOfService = classOfServiceToName.get(classOfServiceEditor.getValue().trim());
		if (ClassOfServicesManager.getInstance().getDefaultClassOfService().getName().equals(selectedClassOfService.getName())) {
			selectedClassOfService = null;
		}

		taskDto.setClassOfService(selectedClassOfService);
		taskDto.setAssignee(userToName.get(assigneeEditor.getValue().trim()));
		if (dueDateCheckBox.getValue()) {
			taskDto.setDueDate(dueDateTextBox.getText().trim());
		} else {
			taskDto.setDueDate("");
		}
		taskDto.setId(getId());
		taskDto.setVersion(getVersion());
		return taskDto;
	}

	protected abstract String getDueDate();

	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			event.stopPropagation();
			event.preventDefault();
			
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

			warningMessages.setText("");
			if (!validate()) {
				return;
			}
			
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
											MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
											DeleteKeyListener.INSTANCE.initialize();
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
			MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
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
	
	class PanelContainingDialogSuggestDisplay extends DefaultSuggestionDisplay {
		
		private PanelContainingDialog dialog;
		
		public PanelContainingDialogSuggestDisplay(PanelContainingDialog dialog) {
			super();
			this.dialog = dialog;
		}


		public void hideSuggestions() {
			super.hideSuggestions();
			dialog.activateEnterEscapeBinding();
		}
	}
	
	class PanelContainingDialogSuggestBox extends SuggestBox {
		
		private PanelContainingDialog dialog;
		
		public PanelContainingDialogSuggestBox(SuggestOracle oracle, PanelContainingDialog dialog) {
			super(oracle, new TextBox(), new PanelContainingDialogSuggestDisplay(dialog));
			this.dialog = dialog;
		}

		@Override
		public void showSuggestionList() {
			super.showSuggestionList();
			dialog.deactivateEnterEscapeBinding();
		}
	}
}
