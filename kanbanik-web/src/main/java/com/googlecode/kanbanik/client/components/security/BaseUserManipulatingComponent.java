package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.managers.PictureResizingLoadHandler;

public abstract class BaseUserManipulatingComponent extends Composite implements PanelContainingDialolgListener, Closable, Component<Dtos.UserDto>, ClickHandler {

	@UiField
	Panel mainPanel;
	
	@UiField
	TextBox username;
	
	@UiField
	TextBox pictureUrl;
	
	@UiField
	TextBox realName;
	
	@UiField
	TextBox password;
	
	@UiField
	HTML messages;
	
	@UiField
	Image assiggneePicturePreview;
	
	@UiField
	Label assigneePicturePreviewLabel;
	
	@UiField
	Label assiggneePicturePreviewErrorLabel;
	
	private PanelContainingDialog dialog;
	
	@Override
	public void close() {
		dialog.close();
	}

	@Override
	public void okClicked(PanelContainingDialog dialog) {
		
		String messageTexts = validate();
		if (!"".equals(messageTexts)) {
			messages.setHTML(messageTexts);
			return;
		}
		
		makeServerCall();
	}

	protected abstract void makeServerCall();

	protected abstract Dtos.UserManipulationDto createDto();
	
	@Override
	public void cancelClicked(PanelContainingDialog dialog) {
		
	}
	

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		dialog = new PanelContainingDialog(title, this, username);
		dialog.addListener(this);
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(Dtos.UserDto dto) {
	}

	@Override
	public void onClick(ClickEvent event) {
		dialog.center();
		
		initialize();
	}

	protected String validate() {
		String messageTexts = "";
		messages.setText("");
		
		if (isEmpty(username)) {
			messageTexts = "<li>The username field must not be emtpy!";
		}
		
		return messageTexts;
	}
	
	protected boolean isEmpty(TextBox textBox) {
		return textBox.getText() == null || textBox.getText().isEmpty();
	}
	
	protected void initialize() {
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
		assigneePicturePreviewLabel.setText("Image Preview");
		if (url == null || "".equals(url)) {
			assiggneePicturePreview.setVisible(false);
			assiggneePicturePreviewErrorLabel.setVisible(true);
			assiggneePicturePreviewErrorLabel.setText("No Picture Set");
		} else {
			assigneePicturePreviewLabel.setText("Image Preview (Loading...)");
			assiggneePicturePreview.addLoadHandler(new PictureResizingLoadHandler(assiggneePicturePreview) {
				@Override
				public void onLoad(LoadEvent event) {
					super.onLoad(event);
					
					assigneePicturePreviewLabel.setText("Image Preview");
					assiggneePicturePreviewErrorLabel.setVisible(false);
				}
			});
			
			assiggneePicturePreview.addErrorHandler(new ErrorHandler() {
				
				@Override
				public void onError(ErrorEvent event) { 
					assigneePicturePreviewLabel.setText("Image Preview");
					assiggneePicturePreviewErrorLabel.setVisible(true);
					assiggneePicturePreviewErrorLabel.setText("Error Loading Image");
					assiggneePicturePreview.setVisible(false);
				}
			});
			assiggneePicturePreview.setUrl(url);	
		}
	}
	
	protected String checkPasswords(TextBox box1, TextBox box2) {
		if (!(isEmpty(box1) && isEmpty(box2))) {
			String pass1 = box1.getText();
			String pass2 = box2.getText();
			if (!pass1.equals(pass2)) {
				return "<li>The password fields must be equal!";
			}
		}
		return "";
	}
}
