package de.bonndan.nivio.notification;

import de.bonndan.nivio.ProcessingEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Queue;

import static de.bonndan.nivio.notification.WebSocketConfig.EVENTS;

@Service
public class MessagingService implements ApplicationListener<ProcessingEvent> {

    //TODO check: events contain references to landscapes. Might result in high memory usage later when landscape change.
    private final Queue<ProcessingEvent> fifo = new CircularFifoQueue<>(1000);
    private final SimpMessagingTemplate template;

    public MessagingService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void onApplicationEvent(ProcessingEvent processingEvent) {
        fifo.add(processingEvent);
        this.template.convertAndSend(WebSocketConfig.TOPIC + EVENTS, processingEvent);
    }

    /**
     * @return the last 1000 processing events
     */
    public ProcessingEvent[] getLast() {
        return fifo.toArray(ProcessingEvent[]::new);
    }
}