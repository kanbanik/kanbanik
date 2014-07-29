package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.*;
import com.googlecode.kanbanik.dto.CommandNames;
import org.atmosphere.gwt20.client.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.googlecode.kanbanik.client.components.ErrorDialog;

public class ServerEventsListener {

    private boolean isActive = false;

    private static final Map<String, Class<?>> eventToDto = new HashMap<String, Class<?>>();
    private static final Map<String, EventAction> eventToAction = new HashMap<String, EventAction>();

    static {
        eventToDto.put(CommandNames.EDIT_TASK.name, Dtos.TaskDto.class);
        eventToDto.put(CommandNames.CREATE_TASK.name, Dtos.TaskDto.class);
        eventToDto.put(CommandNames.DELETE_TASK.name, Dtos.TasksDto.class);
        eventToDto.put(CommandNames.MOVE_TASK.name, Dtos.TaskDto.class);

        eventToAction.put(CommandNames.EDIT_TASK.name, new TaskEditedEventAction());
        eventToAction.put(CommandNames.CREATE_TASK.name, new TaskCreatedEventAction());
        eventToAction.put(CommandNames.DELETE_TASK.name, new TaskDeletedEventAction());
        eventToAction.put(CommandNames.MOVE_TASK.name, new TaskMovedEventAction());
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

    static class TaskMovedEventAction implements EventAction {

        @Override
        public void execute(Pair pair) {
            Dtos.TaskDto newTask = (Dtos.TaskDto) pair.eventPayload;

            GetTaskByIdResponseListener listener = new GetTaskByIdResponseListener(newTask);
            MessageBus.registerListener(GetTaskByIdResponseMessage.class, listener);
            MessageBus.sendMessage(new GetTaskByIdRequestMessage(newTask.getId(), this));
            MessageBus.unregisterListener(GetTaskByIdResponseMessage.class, listener);
        }

        class GetTaskByIdResponseListener implements MessageListener<Dtos.TaskDto> {

            private Dtos.TaskDto newTask = null;

            GetTaskByIdResponseListener(Dtos.TaskDto newTask) {
                this.newTask = newTask;
            }

            @Override
            public void messageArrived(Message<Dtos.TaskDto> message) {
                if (message.getPayload() == null) {
                    return;
                }
                Dtos.TaskDto oldTask = message.getPayload();

                MessageBus.sendMessage(new TaskDeletedMessage(oldTask, this));
                MessageBus.sendMessage(new TaskAddedMessage(newTask, this));
            }
        }
    }

    private void initializeAtmosphere() {
        final AtmosphereRequestConfig jsonRequestConfig = AtmosphereRequestConfig.create(new Serializer());

        jsonRequestConfig.setUrl(GWT.getHostPageBaseURL() + "events/eventSource");
        jsonRequestConfig.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        jsonRequestConfig.setTransport(AtmosphereRequestConfig.Transport.WEBSOCKET);
        jsonRequestConfig.setFallbackTransport(AtmosphereRequestConfig.Transport.LONG_POLLING);
        jsonRequestConfig.setFlags(AtmosphereRequestConfig.Flags.enableProtocol);
        jsonRequestConfig.setFlags(AtmosphereRequestConfig.Flags.trackMessageLength);

        jsonRequestConfig.setOpenHandler(new AtmosphereOpenHandler() {
            @Override
            public void onOpen(AtmosphereResponse response) {
                new ErrorDialog("opened").center();
            }
        });

        jsonRequestConfig.setCloseHandler(new AtmosphereCloseHandler() {
            @Override
            public void onClose(AtmosphereResponse response) {
            }
        });

        jsonRequestConfig.setErrorHandler(new AtmosphereErrorHandler() {
            @Override
            public void onError(AtmosphereResponse response) {
                // error - reconnecting
                final Atmosphere atmosphere = Atmosphere.create();
                atmosphere.subscribe(jsonRequestConfig);
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


        final Atmosphere atmosphere = Atmosphere.create();
        atmosphere.subscribe(jsonRequestConfig);
    }

}


