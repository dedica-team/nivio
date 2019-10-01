package de.bonndan.nivio.stateaggregation;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.StatusDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregatorTest {

    @Mock
    ProviderFactory factory;

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    LandscapeRepository landscapeRepo;

    private Aggregator aggregator;

    private LandscapeImpl landscape;

    private Item item;

    @BeforeEach
    void configureSystemUnderTest() {
        initMocks(this);

        landscape = new LandscapeImpl();
        landscape.setIdentifier("x");

        item = new Item();
        item.setIdentifier("z");
        item.setGroup("y");
        landscape.addItem(item);

        when(landscapeRepo.findDistinctByIdentifier(any())).thenReturn(Optional.of(landscape));
        aggregator = new Aggregator(landscapeRepo, factory, publisher);
    }

    @Test
    public void testWithFactoryErrors() {

        LandscapeDescription e = new LandscapeDescription();
        e.setIdentifier("test");
        e.setStateProviders(List.of(new StateProviderConfig("type", "target")));

        Mockito.when(factory.createFor(Mockito.any(), Mockito.any())).thenThrow(new ProcessingException(e, "fail"));

        aggregator.fetch(e);
        Mockito.verify(publisher, Mockito.atLeastOnce()).publishEvent(Mockito.any(ProcessingErrorEvent.class));
    }

    @Test
    public void testAppliesState() {

        LandscapeDescription landscapeDescription = new LandscapeDescription();
        landscapeDescription.setIdentifier("test");
        landscapeDescription.setStateProviders(List.of(new StateProviderConfig(StateProviderConfig.TYPE_PROMETHEUS_EXPORTER, "target")));

        FullyQualifiedIdentifier fullyQualifiedIdentifier = FullyQualifiedIdentifier.build("x", "y", "z");
        Provider mockProvider = Mockito.mock(Provider.class);
        when(mockProvider.getStates()).then(invocationOnMock -> {
            Map<FullyQualifiedIdentifier, StatusItem> updates = new HashMap<>();
            updates.put(fullyQualifiedIdentifier, new StatusDescription(StatusItem.HEALTH, Status.RED, "error"));
            return updates;
        });
        Mockito.when(factory.createFor(Mockito.any(), Mockito.any()))
                .thenReturn(mockProvider);

        aggregator.fetch(landscapeDescription);
        Set<StatusItem> statuses = item.getStatuses();
        assertFalse(statuses.isEmpty());
        assertEquals(1, statuses.size());
        StatusItem s1 = statuses.iterator().next();
        assertNotNull(s1);
        assertEquals(Status.RED, s1.getStatus());
        assertEquals("error", s1.getMessage());
    }

    @Test
    public void testServiceDeteriorates() {

        item.setStatus(new StatusDescription(StatusItem.HEALTH, Status.GREEN, "ok"));

        LandscapeDescription e = new LandscapeDescription();
        e.setIdentifier("test");
        e.setStateProviders(List.of(new StateProviderConfig(StateProviderConfig.TYPE_PROMETHEUS_EXPORTER, "target")));

        FullyQualifiedIdentifier fullyQualifiedIdentifier = FullyQualifiedIdentifier.build("x", "y", "z");

        Provider mockProvider = Mockito.mock(Provider.class);
        when(mockProvider.getStates()).then(invocationOnMock -> {
            Map<FullyQualifiedIdentifier, StatusItem> updates = new HashMap<>();
            updates.put(fullyQualifiedIdentifier, new StatusDescription(StatusItem.HEALTH, Status.RED, "error"));
            return updates;
        });

        Mockito.when(factory.createFor(Mockito.any(), Mockito.any())).thenReturn(mockProvider);

        aggregator.fetch(e);
        Set<StatusItem> statuses = item.getStatuses();
        assertFalse(statuses.isEmpty());
        assertEquals(1, statuses.size());
        StatusItem s1 = statuses.iterator().next();
        assertNotNull(s1);
        assertEquals(StatusItem.HEALTH, s1.getLabel());
        assertEquals(Status.RED, s1.getStatus());
        assertEquals("error", s1.getMessage());
    }

    @Test
    public void testServiceRecovers() {

        item.setStatus(new StatusDescription(StatusItem.HEALTH, Status.RED, "error"));

        LandscapeDescription e = new LandscapeDescription();
        e.setIdentifier("test");
        e.setStateProviders(List.of(new StateProviderConfig(StateProviderConfig.TYPE_PROMETHEUS_EXPORTER, "target")));
        FullyQualifiedIdentifier fullyQualifiedIdentifier = FullyQualifiedIdentifier.build("x", "y", "z");

        Provider mockProvider = Mockito.mock(Provider.class);
        when(mockProvider.getStates()).then(invocationOnMock -> {
            Map<FullyQualifiedIdentifier, StatusItem> updates = new HashMap<>();
            updates.put(fullyQualifiedIdentifier, new StatusDescription(StatusItem.HEALTH, Status.ORANGE, "better"));
            return updates;
        });

        Mockito.when(factory.createFor(Mockito.any(), Mockito.any())).thenReturn(mockProvider);

        aggregator.fetch(e);

        Set<StatusItem> statuses = item.getStatuses();
        assertFalse(statuses.isEmpty());
        assertEquals(1, statuses.size());
        StatusItem s1 = statuses.iterator().next();
        assertNotNull(s1);
        assertEquals(Status.ORANGE, s1.getStatus());
        assertEquals("better", s1.getMessage());
    }
}
