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
import com.googlecode.kanbanik.client.BaseAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class HeaderComponent extends Composite implements ClickHandler {

	@UiField
	PushButton logoutButton;
	
	@UiField
	Label loggedInUserLabel;
	
	interface MyUiBinder extends UiBinder<Widget, HeaderComponent> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public HeaderComponent() {
		initWidget(uiBinder.createAndBindUi(this));
		
		loggedInUserLabel.setText(CurrentUser.getInstance().getUser().getRealName());
		
		logoutButton.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		new KanbanikServerCaller(new Runnable() {

			public void run() {
				ServerCommandInvokerManager.getInvoker().<VoidParams, VoidParams> invokeCommand(
								ServerCommand.LOGOUT_COMMAND,
								new VoidParams(),
								new BaseAsyncCallback<VoidParams>() {
									
									public void success(VoidParams res) {
										CurrentUser.getInstance().logout();
									}
								});

			}
		});
	}
}
