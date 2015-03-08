package com.googlecode.kanbanik.client.components.task.tag;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.WarningPanel;

public class TagDeletingComponent implements Component<Dtos.TaskTag>, ClickHandler {

    private PanelContainingDialog yesNoDialog;

    private WarningPanel warningPanel;

    private ListBoxWithAddEditDelete<Dtos.TaskTag> parent;

    private Dtos.TaskTag dto;

    @Override
    public void setup(HasClickHandlers clickHandler, String title) {
        clickHandler.addClickHandler(this);
    }

    public void setParent(ListBoxWithAddEditDelete<Dtos.TaskTag> parent) {
        this.parent = parent;
    }

    @Override
    public void setDto(Dtos.TaskTag dto) {
        this.dto = dto;
        warningPanel = new WarningPanel(
                "Are you sure you want to delete class of service '" + dto.getName() + "'?");
    }

    @Override
    public void onClick(ClickEvent event) {
        yesNoDialog = new PanelContainingDialog("Are you sure?", warningPanel);
        yesNoDialog.addListener(new PanelContainingDialog.PanelContainingDialolgListener() {

            @Override
            public void okClicked(PanelContainingDialog dialog) {
                parent.removeItem(dto);
                yesNoDialog.close();
            }

            @Override
            public void cancelClicked(PanelContainingDialog dialog) {

            }
        });
        yesNoDialog.center();
    }

}
