package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.googlecode.kanbanik.client.KanbanikProgressBar;

public class ServerCaller {

    private static final int APP_ERROR_STATUS = 452;

    private static final int USER_NOT_LOGGED_IN_STATUS = 453;

    public static <T, R> void sendRequest(final T dto, final Class<R> responseClass, final ServerCallCallback<R> callback) {
        KanbanikProgressBar.show();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,  URL.encode(GWT.getHostPageBaseURL() + "/api"));
        builder.setHeader("Content-type", "application/x-www-form-urlencoded");

        try {
            Request response = builder.sendRequest(DtoFactory.asJson(dto), new RequestCallback() {

                public void onError(Request request, Throwable exception) {
                    callback.onFailure(exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == APP_ERROR_STATUS) {
                        Dtos.ErrorDto dto = DtoFactory.asDto(Dtos.ErrorDto.class, response.getText());
                        callback.onFailure(dto);
                    } else if (response.getStatusCode() == USER_NOT_LOGGED_IN_STATUS) {
                        Dtos.ErrorDto dto = DtoFactory.asDto(Dtos.ErrorDto.class, response.getText());
                        callback.onUserNotLoggedIn(dto);
                    } else {
                        R responseDto = DtoFactory.asDto(responseClass, response.getText());
                        callback.beforeSuccess(responseDto);
                        callback.onSuccess(responseDto);
                    }
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

}
