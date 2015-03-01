package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PanelWithCheckboxes extends Composite {

    interface MyUiBinder extends UiBinder<Widget, PanelWithCheckboxes> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private BoardsFilter boardsFilter;

    @UiField
    FlowPanel contentPanel;

    @UiField
    Button allButton;

    @UiField
    Button noneButton;

    @UiField
    TextBox filterBox;

    public PanelWithCheckboxes() {

        initWidget(uiBinder.createAndBindUi(this));

        filterBox.getElement().setPropertyString("placeholder", "Filter");

        filterBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String filterText = "";
                if (filterBox.getText() != null) {
                    filterText = filterBox.getText().toLowerCase().trim();
                }

                for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
                    Widget w = contentPanel.getWidget(i);
                    if (w instanceof FilterCheckBox) {
                        final String text = ((FilterCheckBox) w).provideText();
                        if (text != null && text.toLowerCase().contains(filterText)) {
                            w.setVisible(true);
                        } else {
                            w.setVisible(false);
                        }
                    }
                }
            }
        });

        allButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setAllSelected(true);
            }
        });

        noneButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setAllSelected(false);
            }
        });

        allButton.setText("all");
        noneButton.setText("none");
    }

    private void setAllSelected(boolean selected) {

        // don't fire the filter change event for all - do one bulk at the end
        boardsFilter.setIgnoreFilterChanges(true);

        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            Widget w = contentPanel.getWidget(i);
            if (w instanceof FilterCheckBox) {
                ((FilterCheckBox) w).setValue(selected);
                ((FilterCheckBox) w).doValueChanged(selected);
            }
        }

        boardsFilter.setIgnoreFilterChanges(false);
        boardsFilter.fireFilterChangedEvent();
    }

    public void add(Widget w) {
        contentPanel.add(w);
    }

    public void initialize(BoardsFilter boardsFilter) {
        contentPanel.clear();
        this.boardsFilter = boardsFilter;
    }
}
