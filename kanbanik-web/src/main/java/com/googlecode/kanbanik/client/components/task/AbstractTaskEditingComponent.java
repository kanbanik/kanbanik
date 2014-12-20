package com.googlecode.kanbanik.client.components.task;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ResourceClosingCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.DatePickerDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.common.KanbanikRichTextArea;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.kanbanik.client.api.Dtos.ClassOfServiceDto;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

// TODO it start to look really ugly - needs to be cleaned up into ui.xml
public abstract class AbstractTaskEditingComponent {

    private Panel panel;

    private TextArea taskName;

    private Label ticketId;

    private KanbanikRichTextArea description;

    private SuggestBox assigneeEditor;

    private SuggestBox classOfServiceEditor;

    private CheckBox dueDateCheckBox;

    private TextBox dueDateTextBox;

    private PanelContainingDialog dialog;

    private HTML warningMessages;

    private Panel taskDirtyWarningPanel;

    private String name;

    private Map<String, ClassOfServiceDto> classOfServiceToName;

    private Map<String, Dtos.UserDto> userToName;

    private Dtos.BoardDto board;

    private boolean inited = false;

    public AbstractTaskEditingComponent(HasClickHandlers clickHandler, Dtos.BoardDto board) {
        this.name = "Task Details";
        this.board = board;

        clickHandler.addClickHandler(new ShowDialogHandler());
    }

    private void initialize() {
        panel = new FlowPanel();
        taskName = new TextArea();
        ticketId = new Label("");
        dueDateCheckBox = new CheckBox();
        dueDateTextBox = new TextBox();
        warningMessages = new HTML();

        taskDirtyWarningPanel = initTaskDirtyWarningPanel();
        dialog = new PanelContainingDialog(name, panel, taskName, true, 970, 500);

        classOfServiceEditor = new PanelContainingDialogSuggestBox(new MultiWordSuggestOracle(), dialog);
        initSuggestBox(classOfServiceEditor);

        assigneeEditor = new PanelContainingDialogSuggestBox(new MultiWordSuggestOracle(), dialog);
        initSuggestBox(assigneeEditor);

        description = new KanbanikRichTextArea();

        Grid header = new Grid(5, 2);
        header.setWidget(0, 0, new Label("ID"));
        header.setWidget(0, 1, ticketId);

        taskName.setWidth("190px");
        taskName.setHeight("120px");
        taskName.getElement().getStyle().setProperty("resize", "vertical");
        header.setWidget(1, 0, new Label("Short Description"));
        header.setWidget(1, 1, taskName);

        header.setWidget(2, 0, new Label("Class of service"));
        header.setWidget(2, 1, classOfServiceEditor);

        header.setWidget(3, 0, new Label("Assignee"));
        header.setWidget(3, 1, assigneeEditor);

        header.setWidget(4, 0, new Label("Due Date"));

        header.setWidget(4, 1, createDueDatePanel());

        header.setWidth("100%");
        header.getElement().getStyle().setPaddingRight(10, Style.Unit.PX);

        warningMessages.getElement().getStyle().setColor("red");

        FlowPanel controlPanel = new FlowPanel();
        controlPanel.setWidth("100%");
        controlPanel.setHeight("100%");
        controlPanel.getElement().getStyle().setProperty("display", "flex");
        controlPanel.getElement().getStyle().setProperty("flexDirection", "row");
        FlowPanel aroundHeader = new FlowPanel();
        aroundHeader.add(taskDirtyWarningPanel);
        aroundHeader.add(header);
        aroundHeader.setWidth("460px");
        controlPanel.add(aroundHeader);
        controlPanel.add(description);

        panel.add(controlPanel);
        panel.add(warningMessages);
        panel.setWidth("100%");

        dialog.addListener(new AddTaskButtonHandler());
    }

    protected Panel initTaskDirtyWarningPanel() {
        return new FlowPanel();
    }

    private Map<String, ClassOfServiceDto> initClassOfServiceToName(List<ClassOfServiceDto> classesOfService) {
        Map<String, ClassOfServiceDto> res = new HashMap<String, ClassOfServiceDto>();
        for (ClassOfServiceDto classOfService : classesOfService) {
            res.put(classOfService.getName(), classOfService);
        }

        return res;
    }

    private Map<String, Dtos.UserDto> initUserToName(List<Dtos.UserDto> users) {
        Map<String, Dtos.UserDto> res = new HashMap<String, Dtos.UserDto>();
        for (Dtos.UserDto user : users) {
            res.put(user.getUserName(), user);
        }

        return res;
    }

    private Panel createDueDatePanel() {
        final DialogBox datePickerDialog = new DatePickerDialog(dueDateTextBox);

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

    private void fillOracle(Map<String, String> suggestions, MultiWordSuggestOracle oracle) {
        oracle.addAll(suggestions.keySet());

        List<Suggestion> defaults = new ArrayList<Suggestion>();
        for (Map.Entry<String, String> suggestion : suggestions.entrySet()) {
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

    protected void setupValues() {
        ticketId.setText(getTicketId());
        taskName.setValue(getTaskName());
        description.setHtml(getDescription());


        classOfServiceToName = initClassOfServiceToName(ClassOfServicesManager.getInstance().getAll());
        Map<String, String> classOfServiceSuggestionMap = new HashMap<String, String>();
        for (Map.Entry<String, Dtos.ClassOfServiceDto> entry : classOfServiceToName.entrySet()) {
            // enough, no need for description...
            classOfServiceSuggestionMap.put(entry.getKey(), entry.getKey());
        }
        fillOracle(classOfServiceSuggestionMap, (MultiWordSuggestOracle) classOfServiceEditor.getSuggestOracle());
        classOfServiceEditor.setValue(getClassOfServiceAsString());

        userToName = initUserToName(UsersManager.getInstance().getUsers());
        Map<String, String> userSuggestionMap = new HashMap<String, String>();
        for (Map.Entry<String, Dtos.UserDto> entry : userToName.entrySet()) {
            userSuggestionMap.put(entry.getKey(), entry.getKey() + " (" + entry.getValue().getRealName() + ")");
        }
        fillOracle(userSuggestionMap, (MultiWordSuggestOracle) assigneeEditor.getSuggestOracle());
        assigneeEditor.setValue(getUser());

        boolean dueDateSet = getDueDate() != null && !"".equals(getDueDate());
        dueDateCheckBox.setValue(dueDateSet);
        dueDateTextBox.setVisible(dueDateSet);
        dueDateTextBox.setText(getDueDate());

        warningMessages.setText("");
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
        } catch (IllegalArgumentException e) {
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
        Dtos.UserDto newUser = userToName.get(assigneeEditor.getValue().trim());
        if (newUser != null) {
            taskDto.setAssignee(newUser);
        } else {
            taskDto.setAssignee(null);
        }

        if (dueDateCheckBox.getValue()) {
            taskDto.setDueDate(dueDateTextBox.getText().trim());
        } else {
            taskDto.setDueDate("");
        }
        taskDto.setId(getId());
        taskDto.setVersion(getVersion());
        taskDto.setBoardId(board.getId());

        return taskDto;
    }

    protected abstract String getDueDate();

    class ShowDialogHandler implements ClickHandler {

        public void onClick(ClickEvent event) {
            event.stopPropagation();
            event.preventDefault();

            if (!inited) {
                initialize();
                inited = true;
            }

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
            taskDto.setSessionId(CurrentUser.getInstance().getSessionId());

            final boolean isNew = taskDto.getId() == null;
            final CommandNames commandName = isNew ? CommandNames.CREATE_TASK : CommandNames.EDIT_TASK;
            taskDto.setCommandName(commandName.name);

            ServerCaller.<TaskDto, TaskDto>sendRequest(
                    taskDto,
                    TaskDto.class,
                    new ResourceClosingCallback<TaskDto>(dialog) {

                        @Override
                        public void success(TaskDto response) {
                            onHideDialog();
                            if (isNew) {
                                MessageBus.sendMessage(new TaskAddedMessage(response, AbstractTaskEditingComponent.this));
                            } else {
                                MessageBus.sendMessage(new TaskEditedMessage(response, AbstractTaskEditingComponent.this));
                            }
                        }
                    }
            );
        }

        public void cancelClicked(PanelContainingDialog dialog) {
            onHideDialog();
        }

    }

    protected void onHideDialog() {
        MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
        DeleteKeyListener.INSTANCE.initialize();
    }

    class SimpleSuggestion implements Suggestion {

        private Map.Entry<String, String> entry;

        public SimpleSuggestion(Map.Entry<String, String> entry) {
            this.entry = entry;
        }

        @Override
        public String getDisplayString() {
            return entry.getValue();
        }

        @Override
        public String getReplacementString() {
            return entry.getKey();
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
