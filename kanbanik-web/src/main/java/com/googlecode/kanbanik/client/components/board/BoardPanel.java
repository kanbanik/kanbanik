package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class BoardPanel extends Composite {
	
	interface MyUiBinder extends UiBinder<Widget, BoardPanel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    interface SomeRequest {
        String getCommandName();
        void setCommandName(String commandName);

        String getUserName();
        void setUserName(String userName);

        String getPassword();
        void setPassword(String password);
    }

    interface SomeResponse {
        String getResponseText();
        void setResponseText(String responseText);
    }

    interface MyFactory extends AutoBeanFactory {
        AutoBean<SomeRequest> someRequest();
        AutoBean<SomeResponse> someResponseWithDifferentlyNamedMethod();
    }

    MyFactory factory = GWT.create(MyFactory.class);

	@UiField(provided=true)
	Panel projects;
	
	@UiField
	Label boardName;

    @UiField
    PushButton someButton;
	
	public BoardPanel(String name, Panel projects) {
		this.projects = projects;
		initWidget(uiBinder.createAndBindUi(this));
		
		boardName.setText(name);
        someButton.setVisible(false);
        someButton.setText("send it");
        someButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                sendRequest();
            }
        });
	}

    public void sendRequest() {
        String baseDir = GWT.getModuleBaseURL();
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,  URL.encode(GWT.getHostPageBaseURL()+ "api"));
        builder.setHeader("Content-type", "application/x-www-form-urlencoded");

        try {
            Request response = builder.sendRequest( getJsonData(), new RequestCallback() {

                public void onError(Request request, Throwable exception) {

                }

                public void onResponseReceived(Request request, Response response) {
                    AutoBean<SomeResponse> bean = AutoBeanCodex.decode(factory, SomeResponse.class, response.getText());
                    boardName.setText(bean.as().getResponseText());
                }
            });
        } catch (RequestException e) {}
    }

    private String getJsonData() {
        AutoBean<SomeRequest> requestFactory = factory.someRequest();
        SomeRequest request = requestFactory.as();
        request.setCommandName("_some command name_");
        request.setUserName("_user name_");
        request.setPassword("_user pass_");

        AutoBean<SomeRequest> bean = AutoBeanUtils.getAutoBean(request);
        return "command="+ AutoBeanCodex.encode(bean).getPayload();
    }

}