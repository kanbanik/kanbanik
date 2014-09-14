package com.googlecode.kanbanik.client.messaging;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBus {
	
	private static Map<Class<?>, List<MessageListener<?>>> listeners;
	
	public static void sendMessage(Message<?> message) {
		if (message == null) {
			return;
		}

		if (listeners == null) {
			return;
		}
		
		List<MessageListener<?>> listenersForType = getListenersFor(message);
		
		if (listenersForType == null) {
			return;
		}
		
		notifyListeners(message, listenersForType);
	}

    @SuppressWarnings("unchecked")
	private static void notifyListeners(Message<?> message,
			List<MessageListener<?>> listenersForType) {
		
		// the shallow copy is done because the reaction for a sent message can be the calling of unregister something which would lead to concurrent modification exception
		for (@SuppressWarnings("rawtypes") MessageListener listener : new ArrayList<MessageListener<?>>(listenersForType)) {
			listener.messageArrived(message);
		}
	}

	private static List<MessageListener<?>> getListenersFor(
			Message<?> message) {
		List<MessageListener<?>> listenersForType = null;
		for (Class<?> clazz : listeners.keySet()) {
			if (clazz == message.getClass()) {
				listenersForType = listeners.get(clazz);
				break;
			}
		}
		return listenersForType;
	}

    public static void registerOnce(Class<?> messageType, MessageListener<?> listener) {
        if (!listens(messageType, listener)) {
            registerListener(messageType, listener);
        }
    }

	public static void registerListener(Class<?> messageType, MessageListener<?> listener) {
		if (listeners == null) {
			listeners = new HashMap<Class<?>, List<MessageListener<?>>>();
		}

		if (!listeners.containsKey(messageType)) {
			listeners.put(messageType, new ArrayList<MessageListener<?>>());
		}

		listeners.get(messageType).add(listener);
	}
	
	public static void unregisterListener(Class<?> messageType, MessageListener<?> listener) {
		if (listeners == null) {
			return;
		}
		
		if (!listeners.containsKey(messageType)) {
			return;
		}
		
		List<MessageListener<?>> listenersOfType = listeners.get(messageType);

        // in case it has been registered multiple times
        while (listenersOfType.remove(listener)) {
            // just remove it
        }
	}
	
	public static void removeAllListeners() {
		if (listeners == null) {
			return;
		}
		
		listeners.clear();
	}
	
	public static boolean listens(Class<?> messageType, MessageListener<?> toFindOut) {
		if (listeners == null) {
			return false;
		}
		
		if (!listeners.containsKey(messageType)) {
			return false;
		}
		
		for (@SuppressWarnings("rawtypes") MessageListener listener : listeners.get(messageType)) {

			// yes, I'm asking for the specific instance
			if (listener == toFindOut) {
				return true;
			}
		}
		
		return false;
	}
}
