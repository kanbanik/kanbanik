package com.googlecode.kanbanik.client.components.common;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RichTextArea;
import com.googlecode.kanbanik.client.components.task.RichTextToolbar;

public class KanbanikRichTextArea extends Grid {

	private RichTextArea richTextArea;
	
	public KanbanikRichTextArea(String width, String height) { 
		super(2, 1);
		
		richTextArea = new RichTextArea();
	    richTextArea.ensureDebugId("cwRichText-area");
	    richTextArea.setSize(width, height);
	    RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
	    toolbar.ensureDebugId("cwRichText-toolbar");
	    toolbar.setWidth("100%");
	    setStyleName("cw-RichText");
	    setWidget(0, 0, toolbar);
	    setWidget(1, 0, richTextArea);
	}
	
	public KanbanikRichTextArea() {
		this("100%", "400px");
	}
	
	public void setHtml(String html) {
		richTextArea.setHTML(html);
	}
	
	public String getHtml() {
	    return richTextArea.getHTML();
	  }
	
}
