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

    //TODO check: events contain references to landscapes. Might result in high memory usage later when landscape change.
    private final Queue<ProcessingEvent> fifo = new CircularFifoQueue<>(1000);
    private final SimpMessagingTemplate template;

    public MessagingService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void onApplicationEvent(ProcessingEvent processingEvent) {
        fifo.add(processingEvent);
        LOGGER.info("Broadcasting processing event: " + processingEvent.getType());
        this.template.convertAndSend(WebSocketConfig.TOPIC + EVENTS, processingEvent);
    }

    /**
     * @return the last 1000 processing events
     */
    public ProcessingEvent[] getLast() {
        return fifo.toArray(ProcessingEvent[]::new);
    }
}