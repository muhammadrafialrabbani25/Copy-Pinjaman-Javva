package com.p2plending.infrastructure.event;

import com.p2plending.domain.shared.DomainEventPublisher;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleEventBus implements DomainEventPublisher {

    private static final SimpleEventBus INSTANCE = new SimpleEventBus();
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new HashMap<>();
    private final Map<String, List<EventListener>> listenersMap = new HashMap<>();

    private SimpleEventBus() {}

    public static SimpleEventBus getInstance() {
        return INSTANCE;
    }

    @Override
    public void subscribe(String eventType, EventListener listener) {
        listenersMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @Override
    public void publish(Object event) {
        Class<?> eventType = event.getClass();
        if (handlers.containsKey(eventType)) {
            for (Consumer<Object> handler : handlers.get(eventType)) {
                handler.accept(event);
            }
        }
    }

    @Override
    public List<EventListener> getListeners(String eventType) {
        return listenersMap.getOrDefault(eventType, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public <T> void register(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(e -> handler.accept((T) e));
    }
}