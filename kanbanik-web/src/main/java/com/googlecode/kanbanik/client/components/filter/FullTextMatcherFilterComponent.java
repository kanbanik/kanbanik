package com.googlecode.kanbanik.client.components.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.FilterChangedMessage;

import java.util.ArrayList;
import java.util.List;

public class FullTextMatcherFilterComponent extends Composite {

    private BoardsFilter filterObject;

    interface MyUiBinder extends UiBinder<Widget, FullTextMatcherFilterComponent> {}

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    Label mainLabel;

    @UiField
    Label regexIncorrectLabel;

    @UiField
    CheckBox caseSensitive;

    @UiField
    CheckBox regex;

    @UiField
    CheckBox inverse;

    @UiField
    TextArea textArea;

    @UiField
    CheckBox ticketId;

    @UiField
    CheckBox shortDescription;

    @UiField
    CheckBox longDescription;

    public FullTextMatcherFilterComponent(String name) {
        initWidget(uiBinder.createAndBindUi(this));

        mainLabel.setText(name);
        regex.setText("Regex");
        inverse.setText("Inverse");
        caseSensitive.setText("Case Sensitive");

        ticketId.setText("Ticket ID");
        shortDescription.setText("Short Description");
        longDescription.setText("Long Description");
    }

    public void initialize(BoardsFilter filterObject, Dtos.FullTextMatcherDataDto stringMatcherDataDto) {
        this.filterObject = filterObject;

        initValues(stringMatcherDataDto);

        ChangeHandler handler = new ChangeHandler(stringMatcherDataDto);
        regex.addValueChangeHandler(handler);
        inverse.addValueChangeHandler(handler);
        caseSensitive.addValueChangeHandler(handler);

        ticketId.addValueChangeHandler(handler);
        shortDescription.addValueChangeHandler(handler);
        longDescription.addValueChangeHandler(handler);

        textArea.addKeyUpHandler(handler);
    }

    private void initValues(Dtos.FullTextMatcherDataDto stringMatcherDataDto) {
        textArea.setText(stringMatcherDataDto.getString());

        ticketId.setValue(stringMatcherDataDto.getFilteredEntities().contains(Dtos.FilteredEntity.TICKET_ID));
        shortDescription.setValue(stringMatcherDataDto.getFilteredEntities().contains(Dtos.FilteredEntity.SHORT_DESCRIPTION));
        longDescription.setValue(stringMatcherDataDto.getFilteredEntities().contains(Dtos.FilteredEntity.LONG_DESCRIPTION));

        caseSensitive.setValue(stringMatcherDataDto.isCaseSensitive());
        inverse.setValue(stringMatcherDataDto.isInverse());
        regex.setValue(stringMatcherDataDto.isRegex());
        if (regex.getValue()) {
            disableCaseSensitive();
        }
    }

    private void disableCaseSensitive() {
        caseSensitive.setEnabled(false);
        caseSensitive.setTitle("Not possible to set when regex is enabled.");
    }

    class ChangeHandler implements ValueChangeHandler<Boolean>, KeyUpHandler {

        private Dtos.FullTextMatcherDataDto fullTextMatcherDataDto;

        ChangeHandler(Dtos.FullTextMatcherDataDto fullTextMatcherDataDto) {
            this.fullTextMatcherDataDto = fullTextMatcherDataDto;
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            doSynchronize();
        }

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            doSynchronize();
        }

        private void doSynchronize() {
            String string = textArea.getText();
            if ("".equals(string)) {
                string = null;
            }

            if (regex.getValue()) {
                caseSensitive.setValue(false);
                disableCaseSensitive();

                try {
                    RegExp.compile(string);
                } catch (Exception e) {
                    // incorrect regex - let the user know and do not confuse him why is it not working
                    regexIncorrectLabel.setText(" The provided regex is not correct");
                    return;
                }
            } else {
                caseSensitive.setEnabled(true);
                caseSensitive.setTitle("");
            }

            regexIncorrectLabel.setText("");

            fullTextMatcherDataDto.setString(string);
            fullTextMatcherDataDto.setCaseSensitive(caseSensitive.getValue());
            fullTextMatcherDataDto.setInverse(inverse.getValue());
            fullTextMatcherDataDto.setRegex(regex.getValue());

            List<Dtos.FilteredEntity> filteredEntities = new ArrayList<Dtos.FilteredEntity>();

            if (ticketId.getValue()) {
                filteredEntities.add(Dtos.FilteredEntity.TICKET_ID);
            }

            if (shortDescription.getValue()) {
                filteredEntities.add(Dtos.FilteredEntity.SHORT_DESCRIPTION);
            }

            if (longDescription.getValue()) {
                filteredEntities.add(Dtos.FilteredEntity.LONG_DESCRIPTION);
            }

            fullTextMatcherDataDto.setFilteredEntities(filteredEntities);

            MessageBus.sendMessage(new FilterChangedMessage(filterObject, this));
            filterObject.storeFilterData();
        }
    }


}
