package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.DatePickerDialog;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.GetAllBoardsRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.GetAllBoardsResponseMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.GetAllProjectsRequestMessage;
import com.googlecode.kanbanik.client.messaging.messages.project.GetAllProjectsResponseMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.FilterChangedMessage;

import java.util.*;

public class FilterComponent extends Composite {

    interface MyUiBinder extends UiBinder<Widget, FilterComponent> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    DisclosurePanel disclosurePanel;

    @UiField
    FlowPanel userFilter;

    @UiField
    FlowPanel classOfServiceFilter;

    @UiField
    FlowPanel boardFilter;

    @UiField
    FlowPanel projectOnBoardFilter;

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

    private BoardsFilter filterObject;

    public FilterComponent() {
        fullTextFilter = new FullTextMatcherFilterComponent("Textual");

        initWidget(uiBinder.createAndBindUi(this));

        disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                boolean loaded = createFilterObject();

                fullTextFilter.initialize(filterObject, filterObject.getFilterDataDto().getFullTextFilter());

                userFilter.clear();
                classOfServiceFilter.clear();
                boardFilter.clear();
                projectOnBoardFilter.clear();

                fillUsers(filterObject, loaded);
                fillClassOfServices(filterObject, loaded);
                fillBoards(filterObject, loaded);
                fillProjectsOnBoards(filterObject, loaded);
                initDueDate(filterObject);

            }

        });

    }

    private boolean createFilterObject() {
        boolean loaded = true;

        filterObject = new BoardsFilter();
        Dtos.FilterDataDto filterDataDto = filterObject.loadFilterData();

        if (filterDataDto == null) {
            filterDataDto = DtoFactory.filterDataDto();

            filterDataDto.setFullTextFilter(DtoFactory.fullTextMatcherDataDto());
            List<Dtos.FilteredEntity> entities = new ArrayList<Dtos.FilteredEntity>();
            entities.add(Dtos.FilteredEntity.LONG_DESCRIPTION);
            entities.add(Dtos.FilteredEntity.SHORT_DESCRIPTION);
            filterDataDto.getFullTextFilter().setCaseSensitive(false);
            filterDataDto.getFullTextFilter().setInverse(false);
            filterDataDto.getFullTextFilter().setRegex(false);
            filterDataDto.getFullTextFilter().setString("");

            filterDataDto.getFullTextFilter().setFilteredEntities(entities);

            filterDataDto.setClassesOfServices(new ArrayList<Dtos.ClassOfServiceDto>());
            filterDataDto.setUsers(new ArrayList<Dtos.UserDto>());
            filterDataDto.setBoards(new ArrayList<Dtos.BoardDto>());
            filterDataDto.setBoardWithProjectsDto(new ArrayList<Dtos.BoardWithProjectsDto>());

            Dtos.DateMatcherDataDto dueDateFilter = DtoFactory.dateMatcherDataDto();
            dueDateFilter.setCondition(0);
            dueDateFilter.setDateFrom("");
            dueDateFilter.setDateTo("");
            filterDataDto.setDueDate(dueDateFilter);
            loaded = false;
        }

        filterObject.setFilterDataDto(filterDataDto);

        return loaded;
    }

    private void initDueDate(BoardsFilter filterObject) {
        dueDateCondition.clear();
        dueDateCondition.addItem("-------");
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
        if (condition == 4) {
            dueDateToBox.setVisible(true);
        } else {
            dueDateToBox.setVisible(false);
        }

        if (condition == 0) {
            dueDateToBox.setVisible(false);
            dueDateFromBox.setVisible(false);
        } else {
            dueDateFromBox.setVisible(true);
        }
    }

    private void setDueDateToFilterObjectAndFireEvent() {
        Dtos.DateMatcherDataDto dueDateMatches = DtoFactory.dateMatcherDataDto();
        dueDateMatches.setCondition(dueDateCondition.getSelectedIndex());

        dueDateMatches.setDateFrom(dueDateFromBox.getText());
        dueDateMatches.setDateTo(dueDateToBox.getText());
        filterObject.getFilterDataDto().setDueDate(dueDateMatches);
        MessageBus.sendMessage(new FilterChangedMessage(filterObject, this));
        filterObject.storeFilterData();
    }

    private void fillUsers(BoardsFilter filterObject, boolean loaded) {
        List<Dtos.UserDto> sorted = new ArrayList<Dtos.UserDto>(UsersManager.getInstance().getUsers());

        Collections.sort(sorted, new Comparator<Dtos.UserDto>() {
            @Override
            public int compare(Dtos.UserDto userDto, Dtos.UserDto userDto2) {
                return userDto.getUserName().compareTo(userDto2.getUserName());
            }
        });

        sorted.add(0, UsersManager.getInstance().getNoUser());

        for (Dtos.UserDto user : sorted) {
            if (!loaded) {
                filterObject.add(user);
            }
            userFilter.add(new UserFilterCheckBox(user, filterObject));
        }
    }

    private void fillClassOfServices(BoardsFilter filterObject, boolean loaded) {
        List<Dtos.ClassOfServiceDto> sorted = new ArrayList<Dtos.ClassOfServiceDto>(ClassOfServicesManager.getInstance().getAll());

        Collections.sort(sorted, new Comparator<Dtos.ClassOfServiceDto>() {
            @Override
            public int compare(Dtos.ClassOfServiceDto classOfServiceDto, Dtos.ClassOfServiceDto classOfServiceDto2) {
                return classOfServiceDto.getName().compareTo(classOfServiceDto2.getName());
            }
        });


        sorted.add(0, ClassOfServicesManager.getInstance().getDefaultClassOfService());

        for (Dtos.ClassOfServiceDto classOfServiceDto : sorted) {
            if (!loaded) {
                filterObject.add(classOfServiceDto);
            }
            classOfServiceFilter.add(new ClassOfServiceFilterCheckBox(classOfServiceDto, filterObject));
        }
    }

    private DataCollector<Dtos.BoardDto> boardsCollector = new DataCollector<Dtos.BoardDto>();

    private void fillBoards(BoardsFilter filterObject, boolean loaded) {

        MessageBus.unregisterListener(GetAllBoardsResponseMessage.class, boardsCollector);
        MessageBus.registerListener(GetAllBoardsResponseMessage.class, boardsCollector);
        boardsCollector.init();
        MessageBus.sendMessage(new GetAllBoardsRequestMessage(null, this));

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
            if (!loaded) {
                filterObject.add(board);
            }
            boardFilter.add(new BoardsFilterCheckBox(board, filterObject));
        }
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
            if (!loaded) {
                filterObject.add(boardWithProjectDtos);
            }
            projectOnBoardFilter.add(new ProjectOnBoardFilterCheckBox(boardWithProjectDtos, filterObject));
        }
     }

    class DataCollector<T> implements MessageListener<T> {

        private List<T> data;

        @Override
        public void messageArrived(Message<T> message) {
            data.add(message.getPayload());
        }

        public void init() {
            data = new ArrayList<T>();
        }

        public List<T> getData() {
            return data;
        }
    }

    abstract class FilterCheckBox<T> extends CheckBox implements ValueChangeHandler<Boolean> {

        private T entity;

        private BoardsFilter filter;

        public FilterCheckBox(T entity, BoardsFilter filter) {
            this.entity = entity;
            this.filter = filter;

            setWidth("100%");
            getElement().getStyle().setFloat(Style.Float.LEFT);

            addValueChangeHandler(this);

            setText(provideText(entity));
        }

        protected abstract String provideText(T entity);

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            if (event.getValue()) {
                doAdd(entity, filter);
            } else {
                doRemove(entity, filter);
            }

            MessageBus.sendMessage(new FilterChangedMessage(filter, this));
            filter.storeFilterData();
        }

        protected abstract void doAdd(T entity, BoardsFilter filter);
        protected abstract void doRemove(T entity, BoardsFilter filter);
    }

    class UserFilterCheckBox extends FilterCheckBox<Dtos.UserDto> {

        public UserFilterCheckBox(Dtos.UserDto entity, BoardsFilter filter) {
            super(entity, filter);
            setValue(filter.findById(entity) != -1);
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
            setValue(filter.findById(entity) != -1);
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
            setValue(filter.findById(entity) != -1);
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
            setValue(filter.findById(entity) != -1);
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


}