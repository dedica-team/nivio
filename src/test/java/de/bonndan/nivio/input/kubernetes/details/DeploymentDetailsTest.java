package de.bonndan.nivio.input.kubernetes.details;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ServiceItemAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeploymentDetailsTest {
    DeploymentDetails deploymentDetails;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testGetExtendedDetails() {
        var itemAdapter = Mockito.mock(DeploymentItemAdapter.class);
        Mockito.when(itemAdapter.getCreationTimestamp()).thenReturn("creationTimestamp");
        Mockito.when(itemAdapter.getStrategyType()).thenReturn("strategyType");
        Mockito.when(itemAdapter.getNamespace()).thenReturn("namespace");
        Mockito.when(itemAdapter.getName()).thenReturn("name");
        deploymentDetails = new DeploymentDetails(new DefaultDetails());
        Map<String, String> result = deploymentDetails.getExtendedDetails(Map.of(), itemAdapter);
        assertThat(result).isEqualTo(Map.of("name", "name", "namespace", "namespace", "strategy", "strategyType", "creation", "creationTimestamp"));
    }

    @Test
    void testGetExtendedDetailsClassCastException() {
        var itemAdapter = Mockito.mock(ServiceItemAdapter.class);
        Mockito.when(itemAdapter.getCreationTimestamp()).thenReturn("creationTimestamp");
        Mockito.when(itemAdapter.getNamespace()).thenReturn("namespace");
        Mockito.when(itemAdapter.getName()).thenReturn("name");
        Mockito.when(itemAdapter.getType()).thenReturn("testType");
        Mockito.when(itemAdapter.getClusterIP()).thenReturn("testIP");
        Mockito.when(itemAdapter.getSessionAffinity()).thenReturn("testSessionAffinity");
        Logger detailLogger = (Logger) LoggerFactory.getLogger(DeploymentDetails.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        detailLogger.addAppender(listAppender);
        var serviceDetails = new DeploymentDetails(new DefaultDetails());
        serviceDetails.getExtendedDetails(Map.of(), itemAdapter);
        List<ILoggingEvent> logsList = listAppender.list;
        var className = itemAdapter.getClass().getCanonicalName();
        assertThat(logsList.get(0).getMessage()).isEqualTo("class " + className + " cannot be cast to class de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter (" + className + " and de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter are in unnamed module of loader 'app')");
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
    }


    @Test
    void testGetExtendedDetailsException() {
        assertThrows(NullPointerException.class, () -> deploymentDetails.getExtendedDetails(null, null));
    }

}