package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskFilterChangedMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    TextArea shortDescriptionArea;

    @UiField
    TextArea longDescriptionArea;

    @UiField
    TextArea idArea;

    private TaskFilter filterObject;

    public FilterComponent() {
        initWidget(uiBinder.createAndBindUi(this));

        initShortDescription();

        initLongDescription();

        initId();

        disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                filterObject = new TaskFilter();

                userFilter.clear();
                classOfServiceFilter.clear();

                fillUsers(filterObject);
                fillClassOfServices(filterObject);

            }

        });

    }

    private void initId() {
        idArea.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                String text = idArea.getText();
                if ("".equals(text)) {
                    text = null;
                }

                filterObject.setId(text);
                MessageBus.sendMessage(new TaskFilterChangedMessage(filterObject, this));
            }
        });
    }

    private void initLongDescription() {
        longDescriptionArea.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                String text = longDescriptionArea.getText();
                if ("".equals(text)) {
                    text = null;
                }

                filterObject.setLongDescription(text);
                MessageBus.sendMessage(new TaskFilterChangedMessage(filterObject, this));
            }
        });
    }

    private void initShortDescription() {
        shortDescriptionArea.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                String text = shortDescriptionArea.getText();
                if ("".equals(text)) {
                    text = null;
                }

                filterObject.setShortDescription(text);
                MessageBus.sendMessage(new TaskFilterChangedMessage(filterObject, this));
            }
        });
    }

    private void fillUsers(TaskFilter filterObject) {
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

    private void fillClassOfServices(TaskFilter filterObject) {
        List<Dtos.ClassOfServiceDto> sorted = new ArrayList<Dtos.ClassOfServiceDto>(ClassOfServicesManager.getInstance().getAll());

        Collections.sort(sorted, new Comparator<Dtos.ClassOfServiceDto>() {
            @Override
            public int compare(Dtos.ClassOfServiceDto classOfServiceDto, Dtos.ClassOfServiceDto classOfServiceDto2) {
                return classOfServiceDto.getName().compareTo(classOfServiceDto2.getName());
            }
        });

        for (Dtos.ClassOfServiceDto classOfServiceDto : sorted) {
            classOfServiceFilter.add(new ClassOfServiceFilterCheckBox(classOfServiceDto, filterObject));

        }
    }

    abstract class FilterCheckBox<T> extends CheckBox implements ValueChangeHandler<Boolean> {

        private T entity;

        private TaskFilter filter;

        public FilterCheckBox(T entity, TaskFilter filter) {
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

            MessageBus.sendMessage(new TaskFilterChangedMessage(filter, this));
        }

        protected abstract void doAdd(T entity, TaskFilter filter);
        protected abstract void doRemove(T entity, TaskFilter filter);
    }

    class UserFilterCheckBox extends FilterCheckBox<Dtos.UserDto> {

        public UserFilterCheckBox(Dtos.UserDto entity, TaskFilter filter) {
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
        protected void doAdd(Dtos.UserDto entity, TaskFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.UserDto entity, TaskFilter filter) {
            filter.remove(entity);
        }
    }

    class ClassOfServiceFilterCheckBox extends FilterCheckBox<Dtos.ClassOfServiceDto> {

        public ClassOfServiceFilterCheckBox(Dtos.ClassOfServiceDto entity, TaskFilter filter) {
            super(entity, filter);
        }

        @Override
        protected String provideText(Dtos.ClassOfServiceDto entity) {
            return entity.getName();
        }

        @Override
        protected void doAdd(Dtos.ClassOfServiceDto entity, TaskFilter filter) {
            filter.add(entity);
        }

        @Override
        protected void doRemove(Dtos.ClassOfServiceDto entity, TaskFilter filter) {
            filter.remove(entity);
        }
    }


}
