package de.bonndan.nivio.notification;

import de.bonndan.nivio.input.ProcessingEvent;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
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
        ProcessingFinishedEvent processingFinishedEvent = new ProcessingFinishedEvent(
                new LandscapeDescription("test", "testLandscape", null),
                LandscapeFactory.createForTesting("test", "testLandscape").build()
        );
        messagingService.onApplicationEvent(processingFinishedEvent);

        ArgumentCaptor<EventNotification> captor = ArgumentCaptor.forClass(EventNotification.class);
        verify(tpl).convertAndSend(eq(WebSocketConfig.TOPIC + WebSocketConfig.EVENTS), captor.capture());

        EventNotification value = captor.getValue();
        assertNotNull(value);
        assertThat(value.getLevel()).isEqualTo("info");
        assertThat(value.getType()).isEqualTo("ProcessingFinishedEvent");
        assertThat(value.getLandscape()).isEqualTo("test");
    }

    @Test
    void getLast() {

        ProcessingFinishedEvent processingFinishedEvent = new ProcessingFinishedEvent(
                new LandscapeDescription("test", "testLandscape", null),
                LandscapeFactory.createForTesting("test", "testLandscape").build()
        );
        messagingService.onApplicationEvent(processingFinishedEvent);

        EventNotification[] last = messagingService.getLast();
        assertNotNull(last);
        assertEquals(1, last.length);
        assertEquals(processingFinishedEvent.getLandscape().getFullyQualifiedIdentifier().jsonValue(), last[0].getLandscape());
        assertEquals(processingFinishedEvent.getMessage(), last[0].getMessage());
    }
}
