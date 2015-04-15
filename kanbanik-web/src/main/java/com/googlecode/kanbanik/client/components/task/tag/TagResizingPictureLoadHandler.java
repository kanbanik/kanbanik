package com.googlecode.kanbanik.client.components.task.tag;

import com.google.gwt.user.client.ui.Image;
import com.googlecode.kanbanik.client.managers.PictureResizingLoadHandler;

public class TagResizingPictureLoadHandler extends PictureResizingLoadHandler {

    private int maxWidth = 130;

    private Image picture;

    public TagResizingPictureLoadHandler(Image picture) {
        super(picture);
        this.picture = picture;
    }

    @Override
    protected void doResize(int width, int height) {
        if (width <= maxWidth) {
            return;
        }

        float ratio = width / height;
        int newHeight = Math.round(maxWidth / ratio);

        picture.setHeight(newHeight + "px");
        picture.setWidth(maxWidth + "px");
    }
}
