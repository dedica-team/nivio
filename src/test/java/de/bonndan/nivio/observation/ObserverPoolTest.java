package de.bonndan.nivio.observation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

class ObserverPoolTest {

    private ThreadPoolTaskScheduler scheduler;
    private ObserverPool observerPool;
    private ScheduledFuture scheduledFuture;

    @BeforeEach
    void setup() {
        scheduler = mock(ThreadPoolTaskScheduler.class);
        scheduledFuture = mock(ScheduledFuture.class);
        var observerConfigProperties = mock(ObserverConfigProperties.class);
        when(scheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong())).thenReturn(scheduledFuture);

        observerPool = new ObserverPool(scheduler, observerConfigProperties);
    }

    @Test
    void schedulesRunnables() {
        InputFormatObserver observer1 = mock(InputFormatObserver.class);
        InputFormatObserver observer2 = mock(InputFormatObserver.class);
        InputFormatObserver observer3 = mock(InputFormatObserver.class);

        //when
        observerPool.updateObservers(List.of(observer1, observer2, observer3));

        //then
        verify(scheduler, times(3)).scheduleWithFixedDelay(any(Runnable.class), eq(30000L));
    }

    @Test
    void stopsScheduledTasks() {
        InputFormatObserver observer1 = mock(InputFormatObserver.class);
        InputFormatObserver observer2 = mock(InputFormatObserver.class);
        observerPool.updateObservers(List.of(observer1, observer2));

        //when
        InputFormatObserver observer3 = mock(InputFormatObserver.class);
        InputFormatObserver observer4 = mock(InputFormatObserver.class);
        observerPool.updateObservers(List.of(observer3, observer4));

        //then
        verify(scheduledFuture, times(2)).cancel(eq(true));
    }

}