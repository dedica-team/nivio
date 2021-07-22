package de.bonndan.nivio.input.kubernetes.details;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ServiceItemAdapter;
import io.fabric8.kubernetes.api.model.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersistentVolumeDetailsTest {

    PersistentVolumeDetails persistentVolumeDetails;
    PersistentVolumeItemAdapter itemAdapter;

    @BeforeEach
    void setUp() {
        itemAdapter = Mockito.mock(PersistentVolumeItemAdapter.class);
        Mockito.when(itemAdapter.getCreationTimestamp()).thenReturn("creationTimestamp");
        Mockito.when(itemAdapter.getNamespace()).thenReturn("namespace");
        Mockito.when(itemAdapter.getName()).thenReturn("name");
        Mockito.when(itemAdapter.getAccessModes()).thenReturn(List.of("testMode"));
        Mockito.when(itemAdapter.getPhase()).thenReturn("phase");
        Mockito.when(itemAdapter.getPersistentVolumeReclaimPolicy()).thenReturn("persistentVolumeReclaimPolicy");
        Mockito.when(itemAdapter.getCapacity()).thenReturn(Map.of("testCapacity", new Quantity("8", "gi")));
        Mockito.when(itemAdapter.getStorageClassName()).thenReturn("testStorage");
    }

    @Test
    void testGetExtendedDetails() {
        persistentVolumeDetails = new PersistentVolumeDetails(new DefaultDetails());
        Map<String, String> result = persistentVolumeDetails.getExtendedDetails(Map.of(), itemAdapter);
        assertThat(result).isEqualTo(Map.of("creation", "creationTimestamp", "name", "name", "namespace", "namespace", "phase status", "phase", "reclaim policy", "persistentVolumeReclaimPolicy", "storage class", "testStorage", "storage mode", "testMode", "testCapacity", "8gi"));
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
        Logger detailLogger = (Logger) LoggerFactory.getLogger(PersistentVolumeDetails.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        detailLogger.addAppender(listAppender);
        var serviceDetails = new PersistentVolumeDetails(new DefaultDetails());
        serviceDetails.getExtendedDetails(Map.of(), itemAdapter);
        List<ILoggingEvent> logsList = listAppender.list;
        var className = itemAdapter.getClass().getCanonicalName();
        assertThat(logsList.get(0).getMessage()).isEqualTo("class " + className + " cannot be cast to class de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter (" + className + " and de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter are in unnamed module of loader 'app')");
        assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
    }


    @Test
    void testGetExtendedDetailsException() {
        assertThrows(NullPointerException.class, () -> persistentVolumeDetails.getExtendedDetails(null, null));
    }
}
