package de.bonndan.nivio.observation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

class LandscapeObserverPoolTest {

    private ThreadPoolTaskScheduler scheduler;
    private LandscapeObserverPool landscapeObserverPool;
    private ScheduledFuture scheduledFuture;

    @BeforeEach
    void setup() {
        scheduler = mock(ThreadPoolTaskScheduler.class);
        scheduledFuture = mock(ScheduledFuture.class);
        when(scheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong())).thenReturn(scheduledFuture);

        landscapeObserverPool = new LandscapeObserverPool(scheduler, 1);
    }

    @Test
    public void schedulesRunnables() {
        InputFormatObserver observer1 = mock(InputFormatObserver.class);
        InputFormatObserver observer2 = mock(InputFormatObserver.class);
        InputFormatObserver observer3 = mock(InputFormatObserver.class);

        //when
        landscapeObserverPool.updateObservers(List.of(observer1, observer2, observer3));

        //then
        verify(scheduler, times(3)).scheduleWithFixedDelay(any(Runnable.class), eq(1L));
    }

    @Test
    public void stopsScheduledTasks() {
        InputFormatObserver observer1 = mock(InputFormatObserver.class);
        InputFormatObserver observer2 = mock(InputFormatObserver.class);
        landscapeObserverPool.updateObservers(List.of(observer1, observer2));

        //when
        InputFormatObserver observer3 = mock(InputFormatObserver.class);
        InputFormatObserver observer4 = mock(InputFormatObserver.class);
        landscapeObserverPool.updateObservers(List.of(observer3, observer4));

        //then
        verify(scheduledFuture, times(2)).cancel(eq(true));
    }

}