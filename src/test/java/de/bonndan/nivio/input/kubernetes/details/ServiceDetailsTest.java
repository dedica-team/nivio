package de.bonndan.nivio.input.kubernetes.details;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceDetailsTest {
    @Test
    void testGetExtendedDetailsClassCastException() {
        var itemAdapter = Mockito.mock(DeploymentItemAdapter.class);
        Mockito.when(itemAdapter.getCreationTimestamp()).thenReturn("creationTimestamp");
        Mockito.when(itemAdapter.getNamespace()).thenReturn("namespace");
        Mockito.when(itemAdapter.getName()).thenReturn("name");
        Logger detailLogger = (Logger) LoggerFactory.getLogger(ServiceDetails.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        detailLogger.addAppender(listAppender);
        var serviceDetails = new ServiceDetails(new DefaultDetails());
        serviceDetails.getExtendedDetails(Map.of(), itemAdapter);
        List<ILoggingEvent> logsList = listAppender.list;
        var className = itemAdapter.getClass().getCanonicalName();
        assertThat(logsList.get(0).getMessage()).isEqualTo("class " + className + " cannot be cast to class de.bonndan.nivio.input.kubernetes.itemadapters.ServiceItemAdapter (" + className + " and de.bonndan.nivio.input.kubernetes.itemadapters.ServiceItemAdapter are in unnamed module of loader 'app')");
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
    }
}