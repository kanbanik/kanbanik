package com.googlecode.kanbanik.client.components.common;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.kanbanik.client.components.task.tag.TagResizingPictureLoadHandler;
import com.googlecode.kanbanik.client.managers.PictureResizingLoadHandler;

public class PicturePreviewHandler {

    @UiField
    TextBox pictureUrl;

    @UiField
    Image picturePreview;

    @UiField
    Label previewLabel;

    @UiField
    Label picturePreviewErrorLabel;

    public PicturePreviewHandler(TextBox pictureUrl, Image picturePreview, Label previewLabel, Label picturePreviewErrorLabel) {
        this.pictureUrl = pictureUrl;
        this.picturePreview = picturePreview;
        this.previewLabel = previewLabel;
        this.picturePreviewErrorLabel = picturePreviewErrorLabel;
    }

    public void initialize() {
        pictureUrl.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                updateAssigneePicturePreview();
            }
        });

        updateAssigneePicturePreview();
    }


    private void updateAssigneePicturePreview() {
        String url = pictureUrl.getText();
        previewLabel.setText("Image Preview: ");
        if (url == null || "".equals(url)) {
            picturePreview.setVisible(false);
            picturePreviewErrorLabel.setVisible(true);
            picturePreviewErrorLabel.setText("No Picture Set");
        } else {
            previewLabel.setText("Image Preview (Loading...)");
            picturePreview.addLoadHandler(new TagResizingPictureLoadHandler(picturePreview) {
                @Override
                public void onLoad(LoadEvent event) {
                    super.onLoad(event);

                    previewLabel.setText("Image Preview: ");
                    picturePreviewErrorLabel.setVisible(false);
                }

            });

            picturePreview.addErrorHandler(new ErrorHandler() {

                @Override
                public void onError(ErrorEvent event) {
                    previewLabel.setText("Image Preview: ");
                    picturePreviewErrorLabel.setVisible(true);
                    picturePreviewErrorLabel.setText("Error Loading Image");
                    picturePreview.setVisible(false);
                }
            });
            picturePreview.setUrl(url);
        }
    }
}
