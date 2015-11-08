package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.DatePickerDialog;
import com.googlecode.kanbanik.client.components.common.filters.CommonFilterCheckBox;
import com.googlecode.kanbanik.client.components.common.DataCollector;
import com.googlecode.kanbanik.client.components.common.filters.PanelWithCheckboxes;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.TaskTagsManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.board.GetAllBoardsResponseMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.GetBoardsRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.GetAllProjectsRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.GetAllProjectsResponseMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilterComponent extends Composite implements ModulesLifecycleListener, BoardsFilter.NumOfHiddenFieldsChangedListener {

    interface MyUiBinder extends UiBinder<Widget, FilterComponent> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public static final String FILTERS_ACTIVE = "Filtering";

    @UiField
    DisclosurePanel disclosurePanel;

    @UiField
    FilterPanelWithCheckboxes userFilter;

    @UiField
    FilterPanelWithCheckboxes classOfServiceFilter;

    @UiField
    FilterPanelWithCheckboxes boardFilter;

    @UiField
    FilterPanelWithCheckboxes tagsFilter;

    @UiField
    FilterPanelWithCheckboxes projectOnBoardFilter;

    @UiField(provided = true)
    FullTextMatcherFilterComponent fullTextFilter;

    @UiField
    ListBox dueDateCondition;

    DatePickerDialog dueDateFromPicker;

    DatePickerDialog dueDateToPicker;

    @UiField
    TextBox dueDateFromBox;

    @UiField
    TextBox dueDateToBox;

    @UiField
    Label dueDateWarningLabel;

    @UiField
    CheckBox activateFilter;



    private BoardsFilter filterObject;

    public FilterComponent() {
        fullTextFilter = new FullTextMatcherFilterComponent("Contains Text");

        initWidget(uiBinder.createAndBindUi(this));

        try {
            com.google.gwt.dom.client.Element tr = disclosurePanel.getHeader().getElement().getFirstChildElement().getFirstChildElement();
            com.google.gwt.dom.client.Element labelTd = tr.getFirstChildElement().getNextSiblingElement();
            labelTd.getStyle().setColor("#f4faff");
        } catch (Exception e) {
//            quite risky operation only for changing the color of a label - if it will fail than it is better to ignore it and leave the black there and continue than fail the whole app
        }

        activateFilter.setText(FILTERS_ACTIVE);

        new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
    }

    private void init() {
        boolean loaded = createFilterObject();

        fullTextFilter.initialize(filterObject, filterObject.getFilterDataDto().getFullTextFilter());

        userFilter.initialize(filterObject);
        classOfServiceFilter.initialize(filterObject);
        boardFilter.initialize(filterObject);
        tagsFilter.initialize(filterObject);
        projectOnBoardFilter.initialize(filterObject);

        fillTaskTags(filterObject, loaded);
        fillUsers(filterObject, loaded);
        fillClassOfServices(filterObject, loaded);
        fillBoards(filterObject, loaded);
        fillProjectsOnBoards(filterObject, loaded);
        initDueDate(filterObject);
        initActivateFilter(filterObject);

        // using just '|' because I need all the validations to be executed
        if (!fullTextFilter.validate() | !validateDueDate()) {
            return;
        }

        filterObject.fireFilterChangedEvent();
    }

    private void initActivateFilter(final BoardsFilter filterObject) {
        activateFilter.setValue(filterObject.isActive());
        disclosurePanel.setVisible(filterObject.isActive());

        activateFilter.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                boolean active = event.getValue();
                filterObject.setActive(active);
                disclosurePanel.setVisible(active);
                filterObject.fireFilterChangedEvent();
            }
        });
    }

    private boolean createFilterObject() {
        boolean loaded = true;

        filterObject = new BoardsFilter(this);
        Dtos.FilterDataDto filterDataDto = filterObject.loadFilterData();

        if (filterDataDto == null) {
            filterDataDto = DtoFactory.filterDataDto();

            filterDataDto.setFullTextFilter(DtoFactory.fullTextMatcherDataDto());
            List<Dtos.FilteredEntity> entities = new ArrayList<Dtos.FilteredEntity>();
            filterDataDto.getFullTextFilter().setCaseSensitive(false);
            filterDataDto.getFullTextFilter().setInverse(false);
            filterDataDto.getFullTextFilter().setRegex(false);
            filterDataDto.getFullTextFilter().setString("");

            filterDataDto.getFullTextFilter().setFilteredEntities(entities);

            filterDataDto.setClassesOfServices(new ArrayList<Dtos.ClassOfServiceWithSelectedDto>());
            filterDataDto.setUsers(new ArrayList<Dtos.UserWithSelectedDto>());
            filterDataDto.setBoards(new ArrayList<Dtos.BoardWithSelectedDto>());
            filterDataDto.setBoardWithProjectsDto(new ArrayList<Dtos.BoardWithProjectsWithSelectedDto>());
            filterDataDto.setTaskTags(new ArrayList<Dtos.TaskTagWithSelected>());

            Dtos.DateMatcherDataDto dueDateFilter = DtoFactory.dateMatcherDataDto();
            dueDateFilter.setCondition(0);
            dueDateFilter.setDateFrom("");
            dueDateFilter.setDateTo("");
            filterDataDto.setDueDate(dueDateFilter);

            boolean active = filterDataDto.isActive() != null ? filterDataDto.isActive() : true;
            filterDataDto.setActive(active);

            loaded = false;
        }

        filterObject.setFilterDataDto(filterDataDto);

        return loaded;
    }

    private void initDueDate(BoardsFilter filterObject) {
        dueDateCondition.clear();

        dueDateCondition.addItem("-------");
        dueDateCondition.addItem("due date not set");
        dueDateCondition.addItem("less than");
        dueDateCondition.addItem("equals");
        dueDateCondition.addItem("more than");
        dueDateCondition.addItem("between");

        Dtos.DateMatcherDataDto dueDateMatcher = filterObject.getFilterDataDto().getDueDate();

        dueDateCondition.setSelectedIndex(dueDateMatcher.getCondition());
        setDueDateTextBoxVisibility(dueDateMatcher.getCondition());
        dueDateFromBox.setText(dueDateMatcher.getDateFrom());
        dueDateToBox.setText(dueDateMatcher.getDateTo());

        dueDateFromPicker = new DatePickerDialog(dueDateFromBox) {
            @Override
            public void hide() {
                super.hide();
                setDueDateToFilterObjectAndFireEvent();
            }
        };
        dueDateToPicker = new DatePickerDialog(dueDateToBox) {
            @Override
            public void hide() {
                super.hide();
                setDueDateToFilterObjectAndFireEvent();
            }
        };

        dueDateFromBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dueDateFromPicker.show();
            }
        });

        dueDateToBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dueDateToPicker.show();
            }
        });

        dueDateCondition.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                setDueDateTextBoxVisibility(dueDateCondition.getSelectedIndex());

                setDueDateToFilterObjectAndFireEvent();
            }
        });


        dueDateFromBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                setDueDateToFilterObjectAndFireEvent();
            }
        });

        dueDateToBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                setDueDateToFilterObjectAndFireEvent();
            }
        });

    }

    private void setDueDateTextBoxVisibility(int condition) {
        if (condition == BoardsFilter.DATE_CONDITION_BETWEEN) {
            dueDateToBox.setVisible(true);
        } else {
            dueDateToBox.setVisible(false);
        }

        if (condition == BoardsFilter.DATE_CONDITION_UNSET || condition == BoardsFilter.DATE_CONDITION_ONLY_WITHOUT) {
            dueDateToBox.setVisible(false);
            dueDateFromBox.setVisible(false);
        } else {
            dueDateFromBox.setVisible(true);
        }
    }

    private void setDueDateToFilterObjectAndFireEvent() {
        if (!validateDueDate()) {
            return;
        }

        Dtos.DateMatcherDataDto dueDateMatches = DtoFactory.dateMatcherDataDto();
        dueDateMatches.setCondition(dueDateCondition.getSelectedIndex());

        dueDateMatches.setDateFrom(dueDateFromBox.getText());
        dueDateMatches.setDateTo(dueDateToBox.getText());
        filterObject.getFilterDataDto().setDueDate(dueDateMatches);
        filterObject.fireFilterChangedEvent();
    }

    private boolean validateDueDate() {
        dueDateWarningLabel.setText("");

        int dateCondition = dueDateCondition.getSelectedIndex();

        if (dateCondition == BoardsFilter.DATE_CONDITION_UNSET) {
            return true;
        }

        boolean fromFilled = filterObject.parseDate(dueDateFromBox.getText()) != null;
        boolean toFilled = filterObject.parseDate(dueDateToBox.getText()) != null;

        if ((dateCondition == BoardsFilter.DATE_CONDITION_LESS ||
                dateCondition == BoardsFilter.DATE_CONDITION_MORE ||
                dateCondition == BoardsFilter.DATE_CONDITION_EQALS
        ) && !fromFilled
                ) {
                dueDateWarningLabel.setText("Please fill the date");
                return false;
        }

        if (dateCondition == BoardsFilter.DATE_CONDITION_BETWEEN && (!fromFilled || !toFilled)) {
            dueDateWarningLabel.setText("Please fill the date");
            return false;
        }

        return true;
    }

    private void fillUsers(final BoardsFilter filterObject, final boolean loaded) {
        List<Dtos.UserDto> users = UsersManager.getInstance().getUsers();

        users.add(0, UsersManager.getInstance().getNoUser());

        for (Dtos.UserDto user : users) {
            addUser(filterObject, loaded, user);
        }

        UsersManager.getInstance().setListener(new UsersManager.UserChangedListener() {
            @Override
            public void added(Dtos.UserDto user) {
                addUser(filterObject, loaded, user);

                filterObject.fireFilterChangedEvent();
            }

        });
    }

    private void addUser(BoardsFilter filterObject, boolean loaded, Dtos.UserDto user) {
        if (!loaded || filterObject.findById(user) == -1) {
            filterObject.add(user);
        }
        userFilter.add(new UserFilterCheckBox(user, filterObject));
    }


    private void fillClassOfServices(final BoardsFilter filterObject, final boolean loaded) {
        List<Dtos.ClassOfServiceDto> sorted = new ArrayList<Dtos.ClassOfServiceDto>(ClassOfServicesManager.getInstance().getAllWithNone());

        Collections.sort(sorted, new Comparator<Dtos.ClassOfServiceDto>() {
            @Override
            public int compare(Dtos.ClassOfServiceDto classOfServiceDto, Dtos.ClassOfServiceDto classOfServiceDto2) {
                return classOfServiceDto.getName().compareTo(classOfServiceDto2.getName());
            }
        });

        // add default class of service if not present
        if (sorted.size() == 0 || sorted.get(0).getId() != null) {
            sorted.add(0, ClassOfServicesManager.getInstance().getDefaultClassOfService());
        }

        for (Dtos.ClassOfServiceDto classOfServiceDto : sorted) {
            addClassOfService(filterObject, loaded, classOfServiceDto);
        }

        ClassOfServicesManager.getInstance().setListener(new ClassOfServicesManager.ClassOfServiceChangedListener() {
            @Override
            public void added(Dtos.ClassOfServiceDto classOfServiceDto) {
                addClassOfService(filterObject, loaded, classOfServiceDto);

                filterObject.fireFilterChangedEvent();
            }
        });
    }

    private void addClassOfService(BoardsFilter filterObject, boolean loaded, Dtos.ClassOfServiceDto classOfServiceDto) {
        if (!loaded || filterObject.findById(classOfServiceDto) == -1) {
            filterObject.add(classOfServiceDto);
        }
        classOfServiceFilter.add(new ClassOfServiceFilterCheckBox(classOfServiceDto, filterObject));
    }

    private DataCollector<Dtos.BoardDto> boardsCollector = new DataCollector<Dtos.BoardDto>();

    private void fillBoards(BoardsFilter filterObject, boolean loaded) {

        MessageBus.unregisterListener(GetAllBoardsResponseMessage.class, boardsCollector);
        MessageBus.registerListener(GetAllBoardsResponseMessage.class, boardsCollector);
        boardsCollector.init();
        MessageBus.sendMessage(new GetBoardsRequestMessage(null, new GetBoardsRequestMessage.Filter() {
            @Override
            public boolean apply(Dtos.BoardDto boardDto) {
                return true;
            }
        }, this));

        List<Dtos.BoardDto> boards = boardsCollector.getData();
        List<Dtos.BoardDto> shallowBoards = new ArrayList<Dtos.BoardDto>();
        for (Dtos.BoardDto board : boards) {
            shallowBoards.add(asShallowBoard(board));
        }

        Collections.sort(shallowBoards, new Comparator<Dtos.BoardDto>() {
            @Override
            public int compare(Dtos.BoardDto b1, Dtos.BoardDto b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });

        for (Dtos.BoardDto board : shallowBoards) {
            if (!loaded || filterObject.findById(board) == -1) {
                filterObject.add(board);
            }
            boardFilter.add(new BoardsFilterCheckBox(board, filterObject));
        }
    }

    private void fillTaskTags(final BoardsFilter filterObject, final boolean loaded) {
        List<Dtos.TaskTag> tags = TaskTagsManager.getInstance().getTags();

        Collections.sort(tags, new Comparator<Dtos.TaskTag>() {
            @Override
            public int compare(Dtos.TaskTag t1, Dtos.TaskTag t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });

        if (tags.size() == 0 || tags.get(0).getId() != null) {
            tags.add(0, TaskTagsManager.getInstance().noTag());
        }

        if (filterObject.getFilterDataDto().getTaskTags() == null) {
            filterObject.getFilterDataDto().setTaskTags(new ArrayList<Dtos.TaskTagWithSelected>());
        }

        for (Dtos.TaskTag tag : tags) {
            addTag(tag, loaded, filterObject);
        }

        TaskTagsManager.getInstance().setListener(new TaskTagsManager.TagsChangedListener() {
            @Override
            public void added(Dtos.TaskTag tag) {
                addTag(tag, loaded, filterObject);

                filterObject.fireFilterChangedEvent();
            }

            @Override
            public void removed(Dtos.TaskTag tag) {
                removeTag(tag);
            }
        });
    }

    private void removeTag(final Dtos.TaskTag tag) {
        filterObject.deleteFromStorage(tag);

        tagsFilter.remove(new PanelWithCheckboxes.Predicate() {
            @Override
            public boolean toRemove(CommonFilterCheckBox w) {
                Dtos.TaskTag candidate = (Dtos.TaskTag) w.getEntity();
                return objEq(candidate.getName(), tag.getName());
            }

        });

        filterObject.storeFilterData();
    }

    private boolean objEq(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }

        if (o2 == null) {
            return false;
        }

        return o1.equals(o2);
    }

    private void addTag(Dtos.TaskTag tag, boolean loaded, BoardsFilter filterObject) {
        if (!loaded || filterObject.findByName(tag) == -1) {
            filterObject.add(tag);
        }

        tagsFilter.add(new TaskTagFilterCheckBox(tag, filterObject));
    }

    private Dtos.BoardDto asShallowBoard(Dtos.BoardDto board) {
        Dtos.BoardDto shallowBoard = DtoFactory.boardDto();
        shallowBoard.setId(board.getId());
        shallowBoard.setName(board.getName());
        return shallowBoard;
    }


    private DataCollector<Dtos.BoardWithProjectsDto> projectsOnBoardsCollector = new DataCollector<Dtos.BoardWithProjectsDto>();

    private void fillProjectsOnBoards(BoardsFilter filterObject, boolean loaded) {
        MessageBus.unregisterListener(GetAllProjectsResponseMessage.class, projectsOnBoardsCollector);
        MessageBus.registerListener(GetAllProjectsResponseMessage.class, projectsOnBoardsCollector);
        projectsOnBoardsCollector.init();
        MessageBus.sendMessage(new GetAllProjectsRequestMessage(null, this));

        List<Dtos.BoardWithProjectsDto> boardsWithProjectsDtos = projectsOnBoardsCollector.getData();

        List<Dtos.BoardWithProjectsDto> shallowBoardsWithProjectsDtos = new ArrayList<Dtos.BoardWithProjectsDto>();
        for (Dtos.BoardWithProjectsDto boardWithProjectsDto : boardsWithProjectsDtos) {
            Dtos.BoardWithProjectsDto shallowBoardWithProjectsDto = DtoFactory.boardWithProjectsDto();
            shallowBoardWithProjectsDto.setBoard(asShallowBoard(boardWithProjectsDto.getBoard()));
            shallowBoardWithProjectsDto.setProjectsOnBoard(boardWithProjectsDto.getProjectsOnBoard());
            shallowBoardsWithProjectsDtos.add(shallowBoardWithProjectsDto);
        }

        Collections.sort(shallowBoardsWithProjectsDtos, new Comparator<Dtos.BoardWithProjectsDto>() {
            @Override
            public int compare(Dtos.BoardWithProjectsDto b1, Dtos.BoardWithProjectsDto b2) {
                return b1.getProjectsOnBoard().getValues().get(0).getName().compareTo(b2.getProjectsOnBoard().getValues().get(0).getName());
            }
        });

        for (Dtos.BoardWithProjectsDto boardWithProjectDtos : shallowBoardsWithProjectsDtos) {
            if (!loaded || filterObject.findById(boardWithProjectDtos) == -1) {
                filterObject.add(boardWithProjectDtos);
            }
            projectOnBoardFilter.add(new ProjectOnBoardFilterCheckBox(boardWithProjectDtos, filterObject));
        }
     }

    class UserFilterCheckBox extends FilterCheckBox<Dtos.UserDto> {

        public UserFilterCheckBox(Dtos.UserDto entity, BoardsFilter filter) {
            super(entity, filter);
            setValue(filter.isSelected(entity));
        }

        @Override
        protected String provideText(Dtos.UserDto entity) {
            String res = entity.getUserName();
            if (entity.getRealName() != null && !entity.getRealName().equals("")) {
                res += " ("+entity.getRealName()+")";
            }
            return res;
        }

        @Override
        protected void doAdd(Dtos.UserDto entity, BoardsFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.UserDto entity, BoardsFilter filter) {
            filter.remove(entity);
        }
    }

    class BoardsFilterCheckBox extends FilterCheckBox<Dtos.BoardDto> {

        public BoardsFilterCheckBox(Dtos.BoardDto entity, BoardsFilter filter) {
            super(entity, filter);
            setValue(filter.isSelected(entity));
        }

        @Override
        protected String provideText(Dtos.BoardDto entity) {
            return entity.getName();
        }

        @Override
        protected void doAdd(Dtos.BoardDto entity, BoardsFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.BoardDto entity, BoardsFilter filter) {
            filter.remove(entity);
        }
    }

    class ProjectOnBoardFilterCheckBox extends FilterCheckBox<Dtos.BoardWithProjectsDto> {

        public ProjectOnBoardFilterCheckBox(Dtos.BoardWithProjectsDto entity, BoardsFilter filter) {
            super(entity, filter);
            setValue(filter.isSelected(entity));
        }

        @Override
        protected String provideText(Dtos.BoardWithProjectsDto entity) {
            return entity.getProjectsOnBoard().getValues().get(0).getName() + "(" + entity.getBoard().getName() + ")";
        }

        @Override
        protected void doAdd(Dtos.BoardWithProjectsDto entity, BoardsFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.BoardWithProjectsDto entity, BoardsFilter filter) {
            filter.remove(entity);
        }
    }

    class ClassOfServiceFilterCheckBox extends FilterCheckBox<Dtos.ClassOfServiceDto> {

        public ClassOfServiceFilterCheckBox(Dtos.ClassOfServiceDto entity, BoardsFilter filter) {
            super(entity, filter);
            setValue(filter.isSelected(entity));
        }

        @Override
        protected String provideText(Dtos.ClassOfServiceDto entity) {
            return entity.getName();
        }

        @Override
        protected void doAdd(Dtos.ClassOfServiceDto entity, BoardsFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.ClassOfServiceDto entity, BoardsFilter filter) {
            filter.remove(entity);
        }
    }

    class TaskTagFilterCheckBox extends FilterCheckBox<Dtos.TaskTag> {

        public TaskTagFilterCheckBox(Dtos.TaskTag entity, BoardsFilter filter) {
            super(entity, filter);
            setValue(filter.isSelected(entity));
        }

        @Override
        protected String provideText(Dtos.TaskTag entity) {
            return entity.getName();
        }

        @Override
        protected void doAdd(Dtos.TaskTag entity, BoardsFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.TaskTag entity, BoardsFilter filter) {
            filter.remove(entity);
        }
    }

    @Override
    public void activated() {
        init();
    }

    @Override
    public void deactivated() {

    }

    @Override
    public void onNumOfHiddenFieldsChanged(int newNum) {
//        if (!activateFilter.getValue() || newNum == 0) {
//            activateFilter.setText(FILTERS_ACTIVE);
//        } else {
//            activateFilter.setText(FILTERS_ACTIVE + " (" + newNum + " entities match criteria to be hidden)");
//        }
    }
}