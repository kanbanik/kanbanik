package com.googlecode.kanbanik.client.components.common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.googlecode.kanbanik.client.components.task.RichTextToolbar;

import javax.swing.text.Position;

public class KanbanikRichTextArea extends FlowPanel {

	private RichTextArea richTextArea;
	
	public KanbanikRichTextArea(String width, String height, String minHeight) {
		setHeight("100%");
        richTextArea = new RichTextArea();
	    richTextArea.ensureDebugId("cwRichText-area");
	    richTextArea.setSize(width, height);
        richTextArea.getElement().getStyle().setProperty("minHeight", minHeight);
        richTextArea.getElement().getStyle().setProperty("display", "block");

	    RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
	    toolbar.ensureDebugId("cwRichText-toolbar");
	    toolbar.setWidth("100%");

        final DisclosurePanel disclosurePanel = new DisclosurePanel("Editor Toolbar");
        disclosurePanel.add(toolbar);

	    setStyleName("cw-RichText");
        getElement().getStyle().setProperty("position", "compact");
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        getElement().getStyle().setProperty("display", "flex");
        getElement().getStyle().setProperty("flexDirection", "column");
        final FlowPanel aroundDisclosurePanel = new FlowPanel();
        aroundDisclosurePanel.setWidth("100%");
        aroundDisclosurePanel.add(disclosurePanel);

        disclosurePanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                aroundDisclosurePanel.getElement().getStyle().clearHeight();
            }
        });

        disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        aroundDisclosurePanel.getElement().getStyle().setHeight(135, Style.Unit.PX);
                    }
                });
            }
        });

	    add(aroundDisclosurePanel);
	    add(richTextArea);
	}
	
	public KanbanikRichTextArea() {
		this("100%", "100%", "400px");
	}
	
	public void setHtml(String html) {
		richTextArea.setHTML(html);
	}
	
	public String getHtml() {
	    return richTextArea.getHTML();
	  }
	
}
