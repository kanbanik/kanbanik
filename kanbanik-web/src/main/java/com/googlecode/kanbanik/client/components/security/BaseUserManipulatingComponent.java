package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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
import com.googlecode.kanbanik.client.components.common.PicturePreviewHandler;

import java.util.Collections;
import java.util.List;

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

	@UiField
	FlowPanel permissionsPanel;

    private PermissionsEditingComponent permissionsEditingComponent;

	private PanelContainingDialog dialog;

    // may be null for new use
    private Dtos.UserDto oldDto;

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

    protected List<Dtos.PermissionDto> createPermissions() {
        return permissionsEditingComponent.flush();
    }

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		dialog = new PanelContainingDialog(title, this, username);
		dialog.addListener(this);
		clickHandler.addClickHandler(this);
	}

	@Override
	public void setDto(Dtos.UserDto dto) {
        this.oldDto = dto;
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
        new PicturePreviewHandler(pictureUrl, assiggneePicturePreview, assigneePicturePreviewLabel, assiggneePicturePreviewErrorLabel).initialize();
        permissionsEditingComponent = new PermissionsEditingComponent();
        List<Dtos.PermissionDto> permissions = oldDto != null ? oldDto.getPermissions() : Collections.EMPTY_LIST;
        permissions = permissions != null ? permissions : Collections.EMPTY_LIST;
        permissionsEditingComponent.init(permissions);
        permissionsPanel.clear();
        permissionsPanel.add(permissionsEditingComponent);
	}

	protected String checkPasswords(TextBox box1, TextBox box2) {
        if (isEmpty(box1) || isEmpty(box2)) {
            return "<li>The password can not be changed to empty";
        }

		if (!(isEmpty(box1) && isEmpty(box2))) {
			String pass1 = box1.getText();
			String pass2 = box2.getText();
			if (!pass1.equals(pass2)) {
				return "<li>The password fields must be equal";
			}
		}
		return "";
	}
}
