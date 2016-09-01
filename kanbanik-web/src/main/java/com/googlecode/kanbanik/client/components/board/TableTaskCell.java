package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TableTaskCell extends AbstractCell<String> {

    interface Template
            extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<div draggable=\"true\">{0}</div>")
        SafeHtml draggable(String value);
    }

    private static Template template;

    public TableTaskCell() {
        super(BrowserEvents.DRAGSTART, BrowserEvents.DRAGEND, BrowserEvents.DRAGENTER, BrowserEvents.DRAGLEAVE,
                BrowserEvents.DRAGOVER, BrowserEvents.DROP, BrowserEvents.DBLCLICK);

        if (null == template) {
            template = GWT.create(Template.class);
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        sb.append(template.draggable(value));
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                               ValueUpdater<String> valueUpdater) {
        String eventType = event.getType();

        if (BrowserEvents.DROP.equals(eventType)) {
            String player = event.getDataTransfer().getData("task");
            setValue(context, parent, player);
            if (null != valueUpdater) {
                valueUpdater.update(player);
            }
        } else if (BrowserEvents.DRAGSTART.equals(eventType)) {
            event.getDataTransfer().setData("task", value);
            event.getDataTransfer().setData("column", Integer.toString(context.getColumn()));
            event.getDataTransfer().setData("row", Integer.toString(context.getIndex()));
        } else if (BrowserEvents.DBLCLICK.equals(eventType)) {

        }

    }
}
