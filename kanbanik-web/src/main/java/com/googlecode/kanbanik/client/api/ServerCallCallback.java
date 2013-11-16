package com.googlecode.kanbanik.client.api;

import com.google.gwt.http.client.RequestException;
import com.googlecode.kanbanik.client.KanbanikProgressBar;
import com.googlecode.kanbanik.client.components.ErrorDialog;
import com.googlecode.kanbanik.client.security.CurrentUser;

public abstract class ServerCallCallback<T> {

    public void onSuccess(T response) {
        KanbanikProgressBar.hide();
    }

    public void beforeSuccess(T response) {

    }

    public void onUserNotLoggedIn(Dtos.ErrorDto errorDto) {
        KanbanikProgressBar.hide();
        new ErrorDialog("You are no longer logged in").show();
        CurrentUser.getInstance().logoutFrontend();
    }

    public void onFailure(Throwable exception) {
        KanbanikProgressBar.hide();
        new ErrorDialog(exception.getMessage() +
                " exception class: " +
                exception.getClass().getName() +
                ". For details please have a look into server logs.")
        .center();

        anyFailure();
    }

    public void onFailure(Dtos.ErrorDto errorDto) {
        KanbanikProgressBar.hide();
        new ErrorDialog(errorDto.getErrorMessage()).center();

        anyFailure();
    }

    public void anyFailure() {

    }

}
