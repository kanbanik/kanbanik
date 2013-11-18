package com.googlecode.kanbanik.client.api;

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
        onFailure(exception.getMessage() +
                " exception class: " +
                exception.getClass().getName() +
                ". For details please have a look into server logs.");

    }

    public void onFailure(Dtos.ErrorDto errorDto) {
        onFailure(errorDto.getErrorMessage());
    }

    public void onFailure(String errorMsg) {
        KanbanikProgressBar.hide();
        new ErrorDialog(errorMsg).center();

        anyFailure();
    }

    public void anyFailure() {

    }

}
