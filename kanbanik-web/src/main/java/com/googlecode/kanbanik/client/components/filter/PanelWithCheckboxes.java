package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PanelWithCheckboxes extends Composite {

    interface MyUiBinder extends UiBinder<Widget, PanelWithCheckboxes> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FlowPanel contentPanel;

    @UiField
    Button allButton;

    @UiField
    Button noneButton;

    public PanelWithCheckboxes() {
        initWidget(uiBinder.createAndBindUi(this));

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
        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            Widget w = contentPanel.getWidget(i);
            if (w instanceof FilterCheckBox) {
                ((FilterCheckBox) w).setValue(selected);
                ((FilterCheckBox) w).doValueChanged(selected);
            }
        }

    }

    public void add(Widget w) {
        contentPanel.add(w);
    }

    public void clear() {
        contentPanel.clear();
    }
}
