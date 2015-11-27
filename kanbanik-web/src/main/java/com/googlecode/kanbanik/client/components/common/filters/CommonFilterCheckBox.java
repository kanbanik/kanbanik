package com.googlecode.kanbanik.client.components.common.filters;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;

public abstract class CommonFilterCheckBox<T> extends CheckBox implements ValueChangeHandler<Boolean> {

    private T entity;

    public CommonFilterCheckBox(T entity) {
        this.entity = entity;

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
            doAdd(entity);
        } else {
            doRemove(entity);
        }
    }

    protected void doAdd(T entity) {}
    protected void doRemove(T entity) {}

    public String provideText() {
        return provideText(entity);
    }

    public T getEntity() {
        return entity;
    }
}
