package com.googlecode.kanbanik.client;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;

public class KanbanikProgressBar {
	
	private static DialogBox dialog;
	
	static {
		dialog = new DialogBox();
		dialog.setTitle("Communicating with server");
		dialog.setText("Communicating with server");
		dialog.setAnimationEnabled(true);
		dialog.setModal(true);
		dialog.setGlassEnabled(true);
		dialog.add(new Image(KanbanikResources.INSTANCE.progressBarImage()));
	}
	
	public static void show() {
		dialog.center();
	}
	
	public static void hide() {
        if (dialog.isShowing()) {
            dialog.hide();
        }
	}
}
