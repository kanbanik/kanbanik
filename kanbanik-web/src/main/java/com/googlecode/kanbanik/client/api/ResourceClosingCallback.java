package com.googlecode.kanbanik.client.api;

import com.googlecode.kanbanik.client.components.Closable;

public class ResourceClosingCallback<T> extends ServerCallCallback<T> {
    private Closable closable;

    public ResourceClosingCallback(Closable closable) {
        super();
        this.closable = closable;
    }

    @Override
    public void beforeSuccess(T response) {
        super.beforeSuccess(response);

        if (closable != null) {
            closable.close();
        }
    }
}
