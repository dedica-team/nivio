package de.bonndan.nivio.notification;

import de.bonndan.nivio.input.ProcessingEvent;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessagingServiceTest {

    private MessagingService messagingService;
    private SimpMessagingTemplate tpl;

    @BeforeEach
    public void setup() {
        tpl = mock(SimpMessagingTemplate.class);
        messagingService = new MessagingService(tpl);
    }

    @Test
    void onApplicationEvent() {
        ProcessingFinishedEvent processingFinishedEvent = new ProcessingFinishedEvent(new LandscapeDescription(), LandscapeFactory.create("test"));
        messagingService.onApplicationEvent(processingFinishedEvent);

        verify(tpl).convertAndSend(eq(WebSocketConfig.TOPIC + WebSocketConfig.EVENTS), any(ProcessingFinishedEvent.class));
    }

    @Test
    void getLast() {

        ProcessingFinishedEvent processingFinishedEvent = new ProcessingFinishedEvent(new LandscapeDescription(), LandscapeFactory.create("test"));
        messagingService.onApplicationEvent(processingFinishedEvent);

        ProcessingEvent[] last = messagingService.getLast();
        assertNotNull(last);
        assertEquals(1, last.length);
        assertEquals(processingFinishedEvent, last[0]);
    }
}
