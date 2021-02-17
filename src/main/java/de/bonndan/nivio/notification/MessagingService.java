package de.bonndan.nivio.notification;

import de.bonndan.nivio.input.ProcessingEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Queue;

import static de.bonndan.nivio.notification.WebSocketConfig.EVENTS;

/**
 * This services listens for {@link ProcessingEvent}s and broadcasts them to subscribed websocket clients.
 *
 *
 */
@Component
public class MessagingService implements ApplicationListener<ProcessingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingService.class);

    private final Queue<EventNotification> fifo = new CircularFifoQueue<>(1000);
    private final SimpMessagingTemplate template;

    public MessagingService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void onApplicationEvent(ProcessingEvent processingEvent) {
        EventNotification eventNotification = EventNotification.from(processingEvent);
        fifo.add(eventNotification);
        LOGGER.info("Broadcasting processing event: " + processingEvent.getType());
        this.template.convertAndSend(WebSocketConfig.TOPIC + EVENTS, eventNotification);
    }

    /**
     * @return the last 1000 processing events
     */
    public EventNotification[] getLast() {
        return fifo.toArray(EventNotification[]::new);
    }
}