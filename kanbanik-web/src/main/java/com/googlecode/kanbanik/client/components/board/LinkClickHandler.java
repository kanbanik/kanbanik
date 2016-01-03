package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;

public abstract class LinkClickHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
        TextBox linkBox = new TextBox();
        linkBox.setText(getLinkUrl());
        linkBox.selectAll();
        linkBox.getElement().getStyle().setWidth(100, Style.Unit.PCT);

        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label("Please copy this link:"));
        panel.add(linkBox);

        PanelContainingDialog linkDialog = new PanelContainingDialog("Link", panel, linkBox, true, 300, 50);
        linkDialog.hideOKButton();
        linkDialog.center();
    }

    abstract String getLinkUrl();
}
