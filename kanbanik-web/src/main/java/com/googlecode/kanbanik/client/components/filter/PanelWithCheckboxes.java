package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

public class PanelWithCheckboxes extends Composite {

    interface MyUiBinder extends UiBinder<Widget, PanelWithCheckboxes> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FlowPanel contentPanel;

    @UiField
    Button allButton;

    @UiField
    Button noneButton;

    @UiField(provided = true)
    SuggestBox filterBox;

    public PanelWithCheckboxes() {

        filterBox = new SuggestBox(createOracle());
        filterBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                valueChangeEvent.getValue();
                for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
                    Widget w = contentPanel.getWidget(i);
                    if (w instanceof FilterCheckBox) {
                        String candidate = ((FilterCheckBox) w).provideText();
                        String filter = valueChangeEvent.getValue();
                        if (filter == null || "".equals(filter)) {
                            w.setVisible(true);
                        }

                        if (candidate == null || filter == null) {
                            return;
                        }
                        if (candidate.toLowerCase().equals(filter.toLowerCase())) {
                            w.setVisible(true);
                        } else {
                            w.setVisible(false);
                        }

                    }
                }
            }
        });

        filterBox.addFocusListener(new FocusListener() {
            @Override
            public void onFocus(Widget widget) {
                filterBox.showSuggestionList();
            }

            @Override
            public void onLostFocus(Widget widget) {
            }
        });

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

    private SuggestOracle createOracle() {
        return new SuggestOracle() {
            @Override
            public void requestSuggestions(Request request, Callback callback) {
                doFindSuggestions(request, callback, true);
            }

            @Override
            public void requestDefaultSuggestions(Request request, Callback callback) {
                doFindSuggestions(request, callback, false);
            }

            private void doFindSuggestions(Request request, Callback callback, boolean check) {
                List<Suggestion> res = new ArrayList<Suggestion>();
                fillSggestions(request, res, check);

                Response response = new Response(res);
                callback.onSuggestionsReady(request, response);
            }



            private void fillSggestions(Request request, List<Suggestion> res, boolean check) {
                for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
                    Widget w = contentPanel.getWidget(i);
                    if (w instanceof FilterCheckBox) {
                        final String text = ((FilterCheckBox) w).provideText();
                        if (!check || (text != null && text.toLowerCase().contains(request.getQuery().toLowerCase()))) {
                            res.add(new Suggestion() {
                                @Override
                                public String getDisplayString() {
                                    return text;
                                }

                                @Override
                                public String getReplacementString() {
                                    return text;
                                }
                            });

                        }
                    }
                }
            }

        };
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
