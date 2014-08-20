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

        initDueDate();

        disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                createFilterObject();

                fullTextFilter.initialize(filterObject, filterObject.getFilterDataDto().getFullTextFilter());

                userFilter.clear();
                classOfServiceFilter.clear();
                boardFilter.clear();

                fillUsers(filterObject);
                fillClassOfServices(filterObject);
                fillBoards(filterObject);

            }

        });

    }

    private void createFilterObject() {
        filterObject = new BoardsFilter();

        Dtos.FilterDataDto filterDataDto = DtoFactory.filterDataDto();

        filterDataDto.setFullTextFilter(DtoFactory.fullTextMatcherDataDto());
        filterDataDto.setClassesOfServices(new ArrayList<Dtos.ClassOfServiceDto>());
        filterDataDto.setUsers(new ArrayList<Dtos.UserDto>());
        filterDataDto.setBoards(new ArrayList<Dtos.BoardDto>());

        filterObject.setFilterDataDto(filterDataDto);
    }

    private void initDueDate() {
        dueDateCondition.addItem("-------");
        dueDateCondition.addItem("less than");
        dueDateCondition.addItem("equals");
        dueDateCondition.addItem("more than");
        dueDateCondition.addItem("between");

        dueDateFromBox.setVisible(false);
        dueDateToBox.setVisible(false);

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

        dueDateToBox.setVisible(false);

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
                if (dueDateCondition.getSelectedIndex() == 4) {
                    dueDateToBox.setVisible(true);
                } else {
                    dueDateToBox.setVisible(false);
                }

                if (dueDateCondition.getSelectedIndex() == 0) {
                    dueDateToBox.setVisible(false);
                    dueDateFromBox.setVisible(false);
                } else {
                    dueDateFromBox.setVisible(true);
                }

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

    private void setDueDateToFilterObjectAndFireEvent() {
        Dtos.DateMatcherDataDto dueDateMatches = DtoFactory.dateMatcherDataDto();
        dueDateMatches.setCondition(dueDateCondition.getSelectedIndex());

        dueDateMatches.setDateFrom(dueDateFromBox.getText());
        dueDateMatches.setDateTo(dueDateToBox.getText());
        filterObject.getFilterDataDto().setDueDate(dueDateMatches);
        MessageBus.sendMessage(new FilterChangedMessage(filterObject, this));
    }

    private void fillUsers(BoardsFilter filterObject) {
        List<Dtos.UserDto> sorted = new ArrayList<Dtos.UserDto>(UsersManager.getInstance().getUsers());

        Collections.sort(sorted, new Comparator<Dtos.UserDto>() {
            @Override
            public int compare(Dtos.UserDto userDto, Dtos.UserDto userDto2) {
                return userDto.getUserName().compareTo(userDto2.getUserName());
            }
        });

        for (Dtos.UserDto user : sorted) {
            userFilter.add(new UserFilterCheckBox(user, filterObject));
        }
    }

    private void fillClassOfServices(BoardsFilter filterObject) {
        List<Dtos.ClassOfServiceDto> sorted = new ArrayList<Dtos.ClassOfServiceDto>(ClassOfServicesManager.getInstance().getAll());

        Collections.sort(sorted, new Comparator<Dtos.ClassOfServiceDto>() {
            @Override
            public int compare(Dtos.ClassOfServiceDto classOfServiceDto, Dtos.ClassOfServiceDto classOfServiceDto2) {
                return classOfServiceDto.getName().compareTo(classOfServiceDto2.getName());
            }
        });


        sorted.add(0, ClassOfServicesManager.getInstance().getDefaultClassOfService());

        for (Dtos.ClassOfServiceDto classOfServiceDto : sorted) {
            classOfServiceFilter.add(new ClassOfServiceFilterCheckBox(classOfServiceDto, filterObject));
        }
    }

    private BoardsCollector boardsCollector = new BoardsCollector();

    private void fillBoards(BoardsFilter filterObject) {

        MessageBus.unregisterListener(GetAllBoardsResponseMessage.class, boardsCollector);
        MessageBus.registerListener(GetAllBoardsResponseMessage.class, boardsCollector);
        boardsCollector.init();
        MessageBus.sendMessage(new GetAllBoardsRequestMessage(null, this));

        List<Dtos.BoardDto> boards = boardsCollector.getBoards();

        Collections.sort(boards, new Comparator<Dtos.BoardDto>() {
            @Override
            public int compare(Dtos.BoardDto b1, Dtos.BoardDto b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });

        for (Dtos.BoardDto board : boards) {
            boardFilter.add(new BoardsFilterCheckBox(board, filterObject));
        }
    }

    class BoardsCollector implements MessageListener<Dtos.BoardDto> {

        private List<Dtos.BoardDto> boards;

        @Override
        public void messageArrived(Message<Dtos.BoardDto> message) {
            boards.add(message.getPayload());
        }

        public void init() {
            boards = new ArrayList<Dtos.BoardDto>();
        }

        public List<Dtos.BoardDto> getBoards() {
            return boards;
        }
    }

    abstract class FilterCheckBox<T> extends CheckBox implements ValueChangeHandler<Boolean> {

        private T entity;

        private BoardsFilter filter;

        public FilterCheckBox(T entity, BoardsFilter filter) {
            setValue(true);
            this.entity = entity;
            this.filter = filter;

            setWidth("100%");
            getElement().getStyle().setFloat(Style.Float.LEFT);

            addValueChangeHandler(this);

            setText(provideText(entity));
            doAdd(entity, filter);
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
        }

        protected abstract void doAdd(T entity, BoardsFilter filter);
        protected abstract void doRemove(T entity, BoardsFilter filter);
    }

    class UserFilterCheckBox extends FilterCheckBox<Dtos.UserDto> {

        public UserFilterCheckBox(Dtos.UserDto entity, BoardsFilter filter) {
            super(entity, filter);
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

    class ClassOfServiceFilterCheckBox extends FilterCheckBox<Dtos.ClassOfServiceDto> {

        public ClassOfServiceFilterCheckBox(Dtos.ClassOfServiceDto entity, BoardsFilter filter) {
            super(entity, filter);
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
