package com.googlecode.kanbanik.client.components.task.tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.common.ColorPickerComponent;
import com.googlecode.kanbanik.client.components.common.PicturePreviewHandler;

import static com.googlecode.kanbanik.client.components.task.tag.TagConstants.*;

public abstract class BaseTagEditingComponent extends Composite implements Component<Dtos.TaskTag>,PanelContainingDialog.PanelContainingDialolgListener, ClickHandler {

    private ListBoxWithAddEditDelete<Dtos.TaskTag> parent;

    private PanelContainingDialog dialog;

    @UiField
    TextBox name;

    @UiField
    TextBox description;

    @UiField
    Label pictureUrlLabel;

    @UiField
    TextBox pictureUrl;

    @UiField
    TextBox onClickUrl;

    @UiField
    ListBox colorListBox;

    @UiField
    ColorPickerComponent colorPickerComponent;

    @UiField
    Image picturePreview;

    @UiField
    Label picturePreviewErrorLabel;

    @UiField
    Label picturePreviewLabel;

    @UiField
    FlowPanel picturePreviewValue;

    @UiField
    CheckBox usePicture;

    @UiField
    Label warningMessage;

    PicturePreviewHandler picturePreviewHandler;

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

        for (String color: predefinedColors) {
            colorListBox.addItem(color);
        }

        colorListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                boolean customColorPicked = customColorPicked();
                colorPickerComponent.setVisible(customColorPicked);
                if (customColorPicked) {
                    // reinit it to be rendered properly
                    colorPickerComponent.setColor(colorPickerComponent.getColor());
                }
            }
        });
        colorPickerComponent.init();

        usePicture.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                updatePictureVisibility(event.getValue());
            }
        });
    }

    private boolean customColorPicked() {
        return colorListBox.getSelectedIndex() == CUSTOM_INDEX;
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
        warningMessage.setVisible(false);
        picturePreviewHandler = new PicturePreviewHandler(pictureUrl, picturePreview, picturePreviewLabel, picturePreviewErrorLabel);

        edit();

        picturePreviewHandler.initialize();
    }

    protected void edit() {
    }

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
        if (customColorPicked()) {
            taskTag.setColour(colorPickerComponent.getColor());
        } else {
            taskTag.setColour(predefinedColors.get(colorListBox.getSelectedIndex()));
        }

        taskTag.setOnClickTarget("".equals(onClickUrl.getText()) ? Dtos.TagClickTarget.NONE.getId() : Dtos.TagClickTarget.NEW_WINDOW.getId());
        return taskTag;
    }

    protected void setPictureUrl(String pictureUrlStr) {
        boolean pictureUrlSet = pictureUrlStr != null && !"".equals(pictureUrlStr);
        usePicture.setValue(pictureUrlSet);
        pictureUrl.setText(pictureUrlStr);

        updatePictureVisibility(pictureUrlSet);
        picturePreviewHandler.updateAssigneePicturePreview();
    }

    private void updatePictureVisibility(boolean visible) {
        pictureUrlLabel.setVisible(visible);
        pictureUrl.setVisible(visible);
        picturePreviewLabel.setVisible(visible);
        picturePreviewValue.setVisible(visible);
    }

    protected void setColor(String color) {
        if (color == null || "".equals(color)) {
            colorListBox.setSelectedIndex(TRANSPARENT_INDEX);
            colorPickerComponent.setVisible(false);
        } else if (predefinedColors.contains(color)) {
            int index = predefinedColors.indexOf(color);
            colorListBox.setSelectedIndex(index);
            if (index == TRANSPARENT_INDEX) {
                colorPickerComponent.setVisible(false);
            }
        } else {
            colorListBox.setSelectedIndex(CUSTOM_INDEX);
            colorPickerComponent.setColor(color);
        }
    }

    public ListBoxWithAddEditDelete<Dtos.TaskTag> getParentWidget() {
        return parent;
    }

    @Override
    public void okClicked(PanelContainingDialog dialog) {
        dialog.close();
    }

    protected boolean validate() {
        String nameStr = name.getText();
        boolean valid = nameStr == null || "".equals(nameStr);

        warningMessage.setVisible(valid);

        return !valid;
    }

}