package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import org.atmosphere.gwt20.client.*;

public class ServerEventsListener {

    private boolean isActive = false;

    public ServerEventsListener() {
        initializeAtmosphere();
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    private void initializeAtmosphere() {
        ClientSerializer dummySerializer = new ClientSerializer() {
            public Object deserialize(String message) throws SerializationException {
                return message;
            }

            public String serialize(Object message) throws SerializationException {
                return (String) message;
            }

        };

        AtmosphereRequestConfig jsonRequestConfig = AtmosphereRequestConfig.create(dummySerializer);

        jsonRequestConfig.setUrl(GWT.getHostPageBaseURL() + "events/some");
        jsonRequestConfig.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        jsonRequestConfig.setTransport(AtmosphereRequestConfig.Transport.WEBSOCKET);
        jsonRequestConfig.setFallbackTransport(AtmosphereRequestConfig.Transport.LONG_POLLING);
        jsonRequestConfig.setFlags(AtmosphereRequestConfig.Flags.enableProtocol);
        jsonRequestConfig.setFlags(AtmosphereRequestConfig.Flags.trackMessageLength);

        jsonRequestConfig.setOpenHandler(new AtmosphereOpenHandler() {
            @Override
            public void onOpen(AtmosphereResponse response) {

            }
        });

        jsonRequestConfig.setCloseHandler(new AtmosphereCloseHandler() {
            @Override
            public void onClose(AtmosphereResponse response) {

            }
        });
        jsonRequestConfig.setMessageHandler(new AtmosphereMessageHandler() {
            @Override
            public void onMessage(AtmosphereResponse response) {

            }
        });


        Atmosphere atmosphere = Atmosphere.create();
        final AtmosphereRequest rpcRequest = atmosphere.subscribe(jsonRequestConfig);
    }

}
