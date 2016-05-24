package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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

        allLinks.add(createLine("Whole Board", GWT.getHostPageBaseURL() + "#[{\"bid\":\"" + boardDto.getId() + "\"}]"));

        if (projectsOnBoard != null) {
            for (Dtos.ProjectDto projectDto : projectsOnBoard) {
                allLinks.add(createLine("Project: " + projectDto.getName(), GWT.getHostPageBaseURL() + "#[{\"bid\":\"" + boardDto.getId() + "\", \"pid\":\"" + projectDto.getId() + "\"}]"));
            }
        }

        HTML explanation = new HTML("The syntax is: <br />" +
                "<li> <b> All projects on a board by ID: </b> [{\"bid\":\"the board id\"}]" +
                "<li> <b> All projects on a board by name: </b> [{\"bname\":\"the board name\"}]" +
                "<li> <b> One project by IDs: </b> [{\"bid\":\"the board id\", \"pid\":\"the project id\"}]" +
                "<li> <b> One project by names: </b> [{\"bname\":\"the board name\", \"pname\":\"the project name\"}]" +
                "<li> <b> More selectors: </b> [{\"bname\":\"the board name 1\", \"pname\":\"the project name 1\"}, {\"bname\":\"the board name 2\", \"pname\":\"the project name 2\"}]"

        );

        DisclosurePanel detailsPanel = new DisclosurePanel("Detailed Syntax");
        detailsPanel.add(explanation);

        allLinks.add(detailsPanel);

        PanelContainingDialog linkDialog = new PanelContainingDialog("Link", allLinks, null, false, 400, -1);
        linkDialog.setupToMinSize();
        linkDialog.hideOKButton();
        linkDialog.center();
    }

    private Panel createLine(String text, String link) {
        final TextBox box = new TextBox();
        box.setText(link);
        box.setWidth("312px");
        box.getElement().getStyle().setFloat(Style.Float.LEFT);

        PushButton selectAll = new PushButton("Select All");
        selectAll.setWidth("60px");
        selectAll.getElement().getStyle().setFloat(Style.Float.RIGHT);
        selectAll.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        box.selectAll();
                    }
                });
            }
        });

        FlowPanel linkWithBtn = new FlowPanel();
        linkWithBtn.setWidth("100%");
        linkWithBtn.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        linkWithBtn.add(box);
        linkWithBtn.add(selectAll);

        FlowPanel res = new FlowPanel();
        Label label = new Label(text);
        label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        res.add(label);
        res.add(linkWithBtn);
        res.setWidth("100%");


        return res;
    }

}
