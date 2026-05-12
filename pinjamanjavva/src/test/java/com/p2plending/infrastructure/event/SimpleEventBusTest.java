package com.p2plending.infrastructure.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEventBusTest {

    private SimpleEventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = SimpleEventBus.getInstance();
    }

    @Test
    @DisplayName("getInstance() harus selalu return instance yang sama")
    void testSingleton() {
        SimpleEventBus instance1 = SimpleEventBus.getInstance();
        SimpleEventBus instance2 = SimpleEventBus.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("register() lalu publish() harus memanggil handler")
    void testRegisterAndPublish() {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        eventBus.register(String.class, event -> handlerCalled.set(true));
        eventBus.publish("test-event");
        assertTrue(handlerCalled.get());
    }

    @Test
    @DisplayName("publish() harus meneruskan event ke handler dengan benar")
    void testPublishPassesEventToHandler() {
        AtomicReference<String> received = new AtomicReference<>();
        eventBus.register(String.class, event -> received.set(event));
        eventBus.publish("hello");
        assertEquals("hello", received.get());
    }

    @Test
    @DisplayName("publish() event tanpa handler tidak boleh error")
    void testPublishWithNoHandler() {
        assertDoesNotThrow(() -> eventBus.publish(12345));
    }

    @Test
    @DisplayName("subscribe() lalu getListeners() harus return listener yang didaftarkan")
    void testSubscribeAndGetListeners() {
        EventListener listener = new EventListener() {};
        eventBus.subscribe("LOAN_APPROVED", listener);
        List<EventListener> listeners = eventBus.getListeners("LOAN_APPROVED");
        assertTrue(listeners.contains(listener));
    }

    @Test
    @DisplayName("getListeners() untuk eventType yang belum ada harus return list kosong")
    void testGetListenersEmpty() {
        List<EventListener> listeners = eventBus.getListeners("EVENT_TIDAK_ADA");
        assertNotNull(listeners);
        assertTrue(listeners.isEmpty());
    }
}
