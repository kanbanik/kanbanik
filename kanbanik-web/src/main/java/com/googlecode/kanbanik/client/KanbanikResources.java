package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface KanbanikResources extends ClientBundle {
	
	public static final KanbanikResources INSTANCE =  GWT.create(KanbanikResources.class);
	
	@Source("images/edit.png")
	ImageResource editButtonImage();
	
	@Source("images/delete.png")
	ImageResource deleteButtonImage();
	
	@Source("images/add.png")
	ImageResource addButtonImage();

	@Source("images/arrowRight.png")
	ImageResource rightDropArrowImage();
	
	@Source("images/arrowDown.png")
	ImageResource downDropArrowImage();
	
	@Source("images/arrowInside.png")
	ImageResource insideDropArrowImage();
	
	@Source("images/progressbar.gif")
	ImageResource progressBarImage();
}
