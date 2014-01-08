package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.googlecode.kanbanik.client.KanbanikProgressBar;
import static com.googlecode.kanbanik.dto.ErrorCodes.APP_ERROR_STATUS;
import static com.googlecode.kanbanik.dto.ErrorCodes.USER_NOT_LOGGED_IN_STATUS;

public class ServerCaller {

    public static <T, R> void sendRequest(final T dto, final Class<R> responseClass, final ServerCallCallback<R> callback) {
        KanbanikProgressBar.show();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,  URL.encode(GWT.getHostPageBaseURL() + "api"));
        builder.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

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
                        R responseDto = null;
                        try {
                            responseDto = DtoFactory.asDto(responseClass, response.getText());

                        } catch (Throwable t) {
                            callback.onFailure("Unable to deserialize JSON: '" + response.getText() + "'");
                        }

                        if (responseDto != null) {
                            callback.beforeSuccess(responseDto);
                            callback.onSuccess(responseDto);
                        }
                    }
                }
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }


}
