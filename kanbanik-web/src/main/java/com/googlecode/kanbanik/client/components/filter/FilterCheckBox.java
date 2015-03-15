package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.FilterChangedMessage;

public abstract class FilterCheckBox<T> extends CheckBox implements ValueChangeHandler<Boolean> {

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
        doValueChanged(event.getValue());
    }

    public void doValueChanged(boolean newValue) {
        if (newValue) {
            doAdd(entity, filter);
        } else {
            doRemove(entity, filter);
        }

        filter.fireFilterChangedEvent();
    }

    protected abstract void doAdd(T entity, BoardsFilter filter);
    protected abstract void doRemove(T entity, BoardsFilter filter);

    public String provideText() {
        return provideText(entity);
    }

    public T getEntity() {
        return entity;
    }
}
