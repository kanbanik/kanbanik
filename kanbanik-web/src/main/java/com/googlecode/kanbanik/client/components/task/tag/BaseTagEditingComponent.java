package com.googlecode.kanbanik.client.components.task.tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;

public abstract class BaseTagEditingComponent extends Composite implements Component<Dtos.TaskTag>,PanelContainingDialog.PanelContainingDialolgListener, ClickHandler {

    private ListBoxWithAddEditDelete<Dtos.TaskTag> parent;

    private PanelContainingDialog dialog;

    @UiField
    TextBox name;

    @UiField
    TextBox description;

    @UiField
    TextBox pictureUrl;

    @UiField
    TextBox onClickUrl;

    @UiField
    TextBox color;

    private Dtos.TaskTag dto;

    interface MyUiBinder extends
        UiBinder<Widget, BaseTagEditingComponent> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public BaseTagEditingComponent() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setup(HasClickHandlers clickHandler, String title) {
        dialog = new PanelContainingDialog(title, this, name);
        dialog.addListener(this);
        clickHandler.addClickHandler(this);
    }

    @Override
    public void setDto(Dtos.TaskTag dto) {
        this.dto = dto;
    }

    public Dtos.TaskTag getDto() {
        return dto;
    }

    @Override
    public void onClick(ClickEvent event) {
        dialog.center();
        edit();
    }

    protected abstract void edit();

    @Override
    public void cancelClicked(PanelContainingDialog dialog) {

    }

    public void setParent(ListBoxWithAddEditDelete<Dtos.TaskTag> parent) {
        this.parent = parent;
    }

    protected Dtos.TaskTag doFlush(Dtos.TaskTag taskTag) {
        taskTag.setName(name.getText());
        taskTag.setDescription(description.getText());
        taskTag.setPictureUrl(pictureUrl.getText());
        taskTag.setOnClickUrl(onClickUrl.getText());
        taskTag.setColour(color.getText());
        taskTag.setOnClickTarget("".equals(onClickUrl.getText()) ? Dtos.TagClickTarget.NONE.getId() : Dtos.TagClickTarget.NEW_WINDOW.getId());
        return taskTag;
    }

    public ListBoxWithAddEditDelete<Dtos.TaskTag> getParentWidget() {
        return parent;
    }

    @Override
    public void okClicked(PanelContainingDialog dialog) {
        dialog.close();
    }
}
