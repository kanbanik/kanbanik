package com.googlecode.kanbanik.client.components.filter;

import com.googlecode.kanbanik.client.components.common.filters.PanelWithCheckboxes;

public class FilterPanelWithCheckboxes extends PanelWithCheckboxes {

    private BoardsFilter boardsFilter;

    protected void setAllSelected(boolean selected) {
        // don't fire the filter change event for all - do one bulk at the end
        boardsFilter.setIgnoreFilterChanges(true);

        super.setAllSelected(selected);

        boardsFilter.setIgnoreFilterChanges(false);
        boardsFilter.fireFilterChangedEvent();
    }

    public void initialize(BoardsFilter boardsFilter) {
        super.initialize();
        this.boardsFilter = boardsFilter;
    }
}
