package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;

import java.util.List;

public class LinkClickHandler implements ClickHandler {

    private final Dtos.BoardDto boardDto;

    private final List<Dtos.ProjectDto> projectsOnBoard;

    public LinkClickHandler(Dtos.BoardDto boardDto, List<Dtos.ProjectDto> projectsOnBoard) {
        this.boardDto = boardDto;
        this.projectsOnBoard = projectsOnBoard;
    }

    @Override
    public void onClick(ClickEvent event) {
        FlowPanel allLinks = new FlowPanel();
        allLinks.setWidth("100%");

        allLinks.add(new Label("Please copy one of the links below:"));
        allLinks.add(createLine("Whole Board", GWT.getHostPageBaseURL() + "#[{\"bid\":\"" + boardDto.getId() + "\"}]"));

        if (projectsOnBoard != null) {
            for (Dtos.ProjectDto projectDto : projectsOnBoard) {
                allLinks.add(createLine("Project: " + projectDto.getName(), GWT.getHostPageBaseURL() + "#[{\"bid\":\"" + boardDto.getId() + "\", \"pid\":\"" + projectDto.getId() + "\"}]"));
            }
        }

        PanelContainingDialog linkDialog = new PanelContainingDialog("Link", allLinks, null, false, 400, -1);
        linkDialog.setupToMinSize();
        linkDialog.hideOKButton();
        linkDialog.center();
    }

    private Panel createLine(String text, String link) {
        TextBox box = new TextBox();
        box.setText(link);
        box.getElement().getStyle().setWidth(90, Style.Unit.PCT);

        FlowPanel res = new FlowPanel();
        Label label = new Label(text);
        label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        res.add(label);
        res.add(box);
        res.setWidth("100%");


        return res;
    }

}
