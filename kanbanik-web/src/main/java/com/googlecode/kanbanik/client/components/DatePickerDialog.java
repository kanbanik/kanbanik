package com.googlecode.kanbanik.client.components;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

import java.util.Date;

public class DatePickerDialog extends DialogBox {

    private DatePicker dueDatePicker = new DatePicker();
    private TextBox dueDateTextBox;

    public DatePickerDialog(final TextBox dueDateTextBox) {
        this.dueDateTextBox = dueDateTextBox;
        setText("Due Date");
        setWidget(dueDatePicker);
        dueDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            public void onValueChange(ValueChangeEvent<Date> event) {
                Date date = event.getValue();
                String dateString = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(date);
                dueDateTextBox.setText(dateString);
                hide();
            }
        });
    }

    @Override
    public boolean onKeyDownPreview(char key, int modifiers) {
        switch (key) {
            case KeyCodes.KEY_ESCAPE:
                hide();
                break;
        }

        return true;
    }

    @Override
    public void hide() {
        super.hide();
        dueDateTextBox.setFocus(true);
    }

    @Override
    public void show() {
        setPopupPosition(dueDateTextBox.getAbsoluteLeft(), dueDateTextBox.getAbsoluteTop() + 30);
        super.show();
    }
}