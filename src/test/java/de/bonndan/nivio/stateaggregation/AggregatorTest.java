package de.bonndan.nivio.stateaggregation;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.StatusDescription;
import de.bonndan.nivio.landscape.*;
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

    private Landscape landscape;

    private Service service;

    @BeforeEach
    void configureSystemUnderTest() {
        initMocks(this);

        landscape = new Landscape();
        landscape.setIdentifier("x");

        service = new Service();
        service.setIdentifier("z");
        service.setGroup("y");
        landscape.addService(service);

        when(landscapeRepo.findDistinctByIdentifier(any())).thenReturn(Optional.of(landscape));
        aggregator = new Aggregator(landscapeRepo, factory, publisher);
    }

    @Test
    public void testWithFactoryErrors() {

        Environment e = new Environment();
        e.setIdentifier("test");
        e.setStateProviders(List.of(new StateProviderConfig("type", "target")));

        Mockito.when(factory.createFor(Mockito.any(), Mockito.any())).thenThrow(new ProcessingException(e, "fail"));

        aggregator.fetch(e);
        Mockito.verify(publisher, Mockito.atLeastOnce()).publishEvent(Mockito.any(ProcessingErrorEvent.class));
    }

    @Test
    public void testAppliesState() {

        Environment environment = new Environment();
        environment.setIdentifier("test");
        environment.setStateProviders(List.of(new StateProviderConfig(StateProviderConfig.TYPE_PROMETHEUS_EXPORTER, "target")));

        FullyQualifiedIdentifier fullyQualifiedIdentifier = FullyQualifiedIdentifier.build("x", "y", "z");
        Provider mockProvider = Mockito.mock(Provider.class);
        when(mockProvider.getStates()).then(invocationOnMock -> {
            Map<FullyQualifiedIdentifier, StatusItem> updates = new HashMap<>();
            updates.put(fullyQualifiedIdentifier, new StatusDescription(StatusItem.HEALTH, Status.RED, "error"));
            return updates;
        });
        Mockito.when(factory.createFor(Mockito.any(), Mockito.any()))
                .thenReturn(mockProvider);

        aggregator.fetch(environment);
        Set<StatusItem> statuses = service.getStatuses();
        assertFalse(statuses.isEmpty());
        assertEquals(1, statuses.size());
        StatusItem s1 = statuses.iterator().next();
        assertNotNull(s1);
        assertEquals(Status.RED, s1.getStatus());
        assertEquals("error", s1.getMessage());
    }

    @Test
    public void testServiceDeteriorates() {

        service.setStatus(new StatusDescription(StatusItem.HEALTH, Status.GREEN, "ok"));

        Environment e = new Environment();
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
        Set<StatusItem> statuses = service.getStatuses();
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

        service.setStatus(new StatusDescription(StatusItem.HEALTH, Status.RED, "error"));

        Environment e = new Environment();
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

        Set<StatusItem> statuses = service.getStatuses();
        assertFalse(statuses.isEmpty());
        assertEquals(1, statuses.size());
        StatusItem s1 = statuses.iterator().next();
        assertNotNull(s1);
        assertEquals(Status.ORANGE, s1.getStatus());
        assertEquals("better", s1.getMessage());
    }
}
