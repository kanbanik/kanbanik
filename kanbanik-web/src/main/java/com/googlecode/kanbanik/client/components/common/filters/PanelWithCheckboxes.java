package com.googlecode.kanbanik.client.components.common.filters;

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

import java.util.ArrayList;
import java.util.List;

public class PanelWithCheckboxes<T> extends Composite{
    interface MyUiBinder extends UiBinder<Widget, PanelWithCheckboxes> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

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
                    if (w instanceof CommonFilterCheckBox) {
                        final String text = ((CommonFilterCheckBox) w).provideText();
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

    protected void setAllSelected(boolean selected) {
        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            Widget w = contentPanel.getWidget(i);
            if (w instanceof CommonFilterCheckBox) {
                ((CommonFilterCheckBox) w).setValue(selected);
                ((CommonFilterCheckBox) w).doValueChanged(selected);
            }
        }
    }

    public void add(Widget w) {
        contentPanel.add(w);
    }

    public void remove(Predicate predicate) {
        Widget toRemove = null;
        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            Widget w = contentPanel.getWidget(i);
            if (w instanceof CommonFilterCheckBox) {
                if (predicate.toRemove((CommonFilterCheckBox) w)) {
                    toRemove = w;
                    break;
                }
            }
        }

        if (toRemove != null) {
            contentPanel.remove(toRemove);
        }
    }

    public List<CommonFilterCheckBox<T>> getContent() {
        List<CommonFilterCheckBox<T>> res = new ArrayList<>();
        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            Widget w = contentPanel.getWidget(i);
            if (w instanceof CommonFilterCheckBox) {
                res.add((CommonFilterCheckBox<T>) w);
            }
        }

        return res;
    }

    public void initialize() {
        contentPanel.clear();
    }

    public static interface Predicate {
        boolean toRemove(CommonFilterCheckBox w);
    }
}
