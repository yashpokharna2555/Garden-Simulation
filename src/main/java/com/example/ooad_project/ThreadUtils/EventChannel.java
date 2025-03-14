package com.example.ooad_project.ThreadUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventChannel {
    // Thread-safe map to store event listeners
    private static final Map<String, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    /**
     * Subscribes a listener to a specific event type.
     *
     * @param eventType the type of event to listen for.
     * @param listener  the callback function to invoke when the event is published.
     */
    public static void subscribe(String eventType, Consumer<Object> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Publishes an event of a specific type, notifying all subscribed listeners.
     *
     * @param eventType the type of event being published.
     * @param event     the event object to pass to the listeners.
     */
    public static void publish(String eventType, Object event) {
        List<Consumer<Object>> eventListeners = listeners.getOrDefault(eventType, Collections.emptyList());
        eventListeners.forEach(listener -> {
            try {
                listener.accept(event);
            } catch (Exception e) {
                // Log or handle exceptions thrown by listeners
                System.err.println("Error notifying listener for event type '" + eventType + "': " + e.getMessage());
            }
        });
    }

    /**
     * Removes a specific listener for a given event type.
     *
     * @param eventType the type of event.
     * @param listener  the listener to remove.
     */
    public static void unsubscribe(String eventType, Consumer<Object> listener) {
        List<Consumer<Object>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            if (eventListeners.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }

    /**
     * Removes all listeners for a specific event type.
     *
     * @param eventType the type of event to clear listeners for.
     */
    public static void clearListeners(String eventType) {
        listeners.remove(eventType);
    }

    /**
     * Clears all event listeners for all event types.
     */
    public static void clearAllListeners() {
        listeners.clear();
    }
}