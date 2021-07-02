package de.bonndan.nivio.notification;

import de.bonndan.nivio.input.ProcessingErrorEvent;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.observation.InputChangedEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Queue;

import static de.bonndan.nivio.notification.WebSocketConfig.EVENTS;

/**
 * This services listens for events and broadcasts them to subscribed websocket clients.
 *
 *
 */
@Component
public class MessagingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingService.class);

    private final Queue<EventNotification> fifo = new CircularFifoQueue<>(1000);
    private final SimpMessagingTemplate template;

    public MessagingService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener(ProcessingFinishedEvent.class)
    public void onProcessingFinishedEvent(ProcessingFinishedEvent processingEvent) {
        EventNotification eventNotification = EventNotification.from(processingEvent);
        fifo.add(eventNotification);
        LOGGER.debug("Broadcasting processing event: " + processingEvent.getType());
        this.template.convertAndSend(WebSocketConfig.TOPIC + EVENTS, eventNotification);
    }

    @EventListener(ProcessingErrorEvent.class)
    public void onProcessingErrorEvent(ProcessingErrorEvent processingEvent) {
        EventNotification eventNotification = EventNotification.from(processingEvent);
        fifo.add(eventNotification);
        LOGGER.debug("Broadcasting processing event: " + processingEvent.getType());
        this.template.convertAndSend(WebSocketConfig.TOPIC + EVENTS, eventNotification);
    }

    @EventListener(InputChangedEvent.class)
    public void onInputChangedEvent(InputChangedEvent inputChangedEvent) {
        EventNotification eventNotification = EventNotification.from(inputChangedEvent);
        fifo.add(eventNotification);
        LOGGER.debug("Broadcasting input change event.");
        this.template.convertAndSend(WebSocketConfig.TOPIC + EVENTS, eventNotification);
    }

    /**
     * @return the last 1000 processing events
     */
    public EventNotification[] getLast() {
        return fifo.toArray(EventNotification[]::new);
    }
}