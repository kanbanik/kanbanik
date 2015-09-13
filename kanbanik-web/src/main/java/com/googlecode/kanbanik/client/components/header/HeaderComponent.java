package com.googlecode.kanbanik.client.components.header;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.user.UserEditedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;

public class HeaderComponent extends Composite implements ClickHandler {

	@UiField
	PushButton logoutButton;
	
	@UiField
	Label loggedInUserLabel;
	
	interface MyUiBinder extends UiBinder<Widget, HeaderComponent> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public HeaderComponent() {
		initWidget(uiBinder.createAndBindUi(this));
		
		MessageBus.registerListener(UserEditedMessage.class, new MessageListener<Dtos.UserDto>() {

            @Override
            public void messageArrived(Message<Dtos.UserDto> message) {
                if (message.getPayload().getUserName().equals(CurrentUser.getInstance().getUser().getUserName())) {
                    setupLabel(message.getPayload());
                }
            }
        });
		
		setupLabel(CurrentUser.getInstance().getUser());
        String sessionId = CurrentUser.getInstance().getUser().getSessionId();
        boolean loggedInUser = !(sessionId == null || "".equals(sessionId));
        logoutButton.setText(loggedInUser ? "Logout" : "Login");
		
		logoutButton.addClickHandler(this);
	}
	
	private void setupLabel(Dtos.UserDto user) {
		if (user.getRealName() != null && !user.getRealName().isEmpty()) {
			loggedInUserLabel.setText(user.getRealName());	
		} else {
			loggedInUserLabel.setText(user.getUserName());
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		CurrentUser.getInstance().logout();
	}
}
