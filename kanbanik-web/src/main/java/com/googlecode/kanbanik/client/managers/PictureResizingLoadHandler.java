package com.googlecode.kanbanik.client.managers;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public abstract class PictureResizingLoadHandler implements LoadHandler {
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

		doResize(width, height);

		picture.setVisible(true);
	}

	protected abstract void doResize(int width, int height);
}
