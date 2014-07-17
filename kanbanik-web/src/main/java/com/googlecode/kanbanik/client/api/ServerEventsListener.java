package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.dto.CommandNames;
import org.atmosphere.gwt20.client.*;

import javax.sql.StatementEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerEventsListener {

    private boolean isActive = false;

    private static final Map<String, Class<?>> eventToDto = new HashMap<String, Class<?>>();
    private static final Map<String, EventAction> eventToAction = new HashMap<String, EventAction>();

    static {
        eventToDto.put(CommandNames.EDIT_TASK.name, Dtos.TaskDto.class);
        eventToDto.put(CommandNames.CREATE_TASK.name, Dtos.TaskDto.class);
        eventToDto.put(CommandNames.DELETE_TASK.name, Dtos.TasksDto.class);

        eventToAction.put(CommandNames.EDIT_TASK.name, new TaskEditedEventAction());
        eventToAction.put(CommandNames.CREATE_TASK.name, new TaskCreatedEventAction());
        eventToAction.put(CommandNames.DELETE_TASK.name, new TaskDeletedEventAction());
    }

    public ServerEventsListener() {
        initializeAtmosphere();
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    class Pair {
        String eventName;
        Object eventPayload;

        Pair(String eventName, Object eventPayload) {
            this.eventName = eventName;
            this.eventPayload = eventPayload;
        }
    }

    class Serializer implements ClientSerializer {
        public Object deserialize(String message) throws SerializationException {
            if (!isActive) {
                return null;
            }

            try {
                Dtos.EventDto eventDto = DtoFactory.asDto(Dtos.EventDto.class, message);
                String eventSource = eventDto.getSource();
                if (!eventToDto.containsKey(eventSource)) {
                    // ignore if can not process
                    return null;
                }

                Class<?> dtoClass = eventToDto.get(eventSource);
                Object eventPayload = DtoFactory.asDto(dtoClass, eventDto.getPayload());
                return new Pair(eventSource, eventPayload);
            } catch (Throwable t) {
                // nothing to do - ignoring if a malformed message came here
                return null;
            }
        }

        public String serialize(Object message) throws SerializationException {
            return (String) message;
        }
    }

    interface EventAction {
        void execute(Pair pair);
    }

    static class TaskEditedEventAction implements EventAction {

        @Override
        public void execute(Pair pair) {
            MessageBus.sendMessage(new TaskEditedMessage((Dtos.TaskDto) pair.eventPayload, this));
        }
    }

    static class TaskCreatedEventAction implements EventAction {

        @Override
        public void execute(Pair pair) {
            MessageBus.sendMessage(new TaskAddedMessage((Dtos.TaskDto) pair.eventPayload, this));
        }
    }

    static class TaskDeletedEventAction implements EventAction {

        @Override
        public void execute(Pair pair) {
            for (Dtos.TaskDto taskDto : ((Dtos.TasksDto) pair.eventPayload).getValues()){
                MessageBus.sendMessage(new TaskDeletedMessage(taskDto, this));
            }

        }
    }

    private void initializeAtmosphere() {
        AtmosphereRequestConfig jsonRequestConfig = AtmosphereRequestConfig.create(new Serializer());

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
                if (!isActive) {
                    return;
                }

                List<Pair> pairs = response.getMessages();
                for (Pair pair : pairs) {
                    if (eventToAction.containsKey(pair.eventName)) {
                        eventToAction.get(pair.eventName).execute(pair);
                    }
                }
            }
        });


        Atmosphere atmosphere = Atmosphere.create();
        final AtmosphereRequest rpcRequest = atmosphere.subscribe(jsonRequestConfig);
    }

}


