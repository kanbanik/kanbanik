package com.googlecode.kanbanik.client;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

public class KanbanikProgressBar {
	
	private static DialogBox dialog;
	
	static {
		dialog = new DialogBox();
		dialog.setTitle("Communicating with server");
		dialog.setText("Communicating with server");
		dialog.setAnimationEnabled(false);
		dialog.setModal(true);
		dialog.setGlassEnabled(true);
		dialog.add(new Label("Please wait..."));
	}
	
	public static void show() {
		dialog.center();
	}
	
	public static void hide() {
		dialog.hide();
	}
}
