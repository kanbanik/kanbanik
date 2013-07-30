package com.googlecode.kanbanik.client.managers;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class PictureResizingLoadHandler implements LoadHandler {
	private Image picture;

	private int expectedHeight = 40;

	public PictureResizingLoadHandler(Image picture) {
		this.picture = picture;
	}

	@Override
	public void onLoad(LoadEvent event) {
		int width = picture.getWidth();
		int height = picture.getHeight();

		if (width == 0 || height == 0) {
			return;
		}

		float ratio = height / width;
		int newWidth = Math.round(expectedHeight / ratio);
		picture.setHeight(expectedHeight + "px");
		picture.setWidth(newWidth + "px");
		picture.setVisible(true);
	}
}
