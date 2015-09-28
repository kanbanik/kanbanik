package com.googlecode.kanbanik.client.components.filter;

import com.googlecode.kanbanik.client.components.common.filters.CommonFilterCheckBox;

public abstract class FilterCheckBox<T> extends CommonFilterCheckBox<T> {

    private BoardsFilter filter;

    public FilterCheckBox(T entity, BoardsFilter filter) {
        super(entity);
        this.filter = filter;
    }

    public void doValueChanged(boolean newValue) {
        super.doValueChanged(newValue);

        filter.fireFilterChangedEvent();
    }

    @Override
    protected void doAdd(T entity) {
        doAdd(entity, filter);
    }
    protected void doRemove(T entity) {
        doRemove(entity, filter);
    }

    protected abstract void doAdd(T entity, BoardsFilter filter);
    protected abstract void doRemove(T entity, BoardsFilter filter);

}
