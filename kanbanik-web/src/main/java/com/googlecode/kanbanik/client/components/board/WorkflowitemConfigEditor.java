package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

class WorkflowitemConfigEditor extends Composite {

    @UiField
    TextBox widthBox;

    interface MyUiBinder extends UiBinder<FlowPanel, WorkflowitemConfigEditor> {

    }
    private static WorkflowitemConfigEditor.MyUiBinder uiBinder = GWT.create(WorkflowitemConfigEditor.MyUiBinder.class);

    public WorkflowitemConfigEditor() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public String getWidth() {
        return widthBox.getText();
    }
}
