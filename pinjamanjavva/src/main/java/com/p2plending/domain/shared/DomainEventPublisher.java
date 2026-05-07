package com.p2plending.domain.shared;

import java.util.EventListener;
import java.util.List;

public interface DomainEventPublisher {
    void subscribe(String eventType, EventListener listener);
    /* Subscribe listener untuk menerima event type tertentu */
    void publish(Object event);
    /* Publish event ke semua listeners yang subscribe */
    List <EventListener> getListeners(String eventType);
    /* Get semua listeners untuk event type tertentu */
}