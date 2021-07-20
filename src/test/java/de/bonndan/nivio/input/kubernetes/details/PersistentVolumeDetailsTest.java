package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter;
import io.fabric8.kubernetes.api.model.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        persistentVolumeDetails = new PersistentVolumeDetails(new ServiceDetails(new DefaultDetails()));
        Map<String, String> result = PersistentVolumeDetailsTest.this.persistentVolumeDetails.getExtendedDetails(Map.of(), itemAdapter);
        assertThat(result).isEqualTo(Map.of("creation", "creationTimestamp", "name", "name", "namespace", "namespace", "phase status", "phase", "reclaim policy", "persistentVolumeReclaimPolicy", "storage class", "testStorage", "storage mode", "testMode", "testCapacity", "8gi"));
    }


    @Test
    void testGetExtendedDetailsException() {
        assertThrows(NullPointerException.class, () -> persistentVolumeDetails.getExtendedDetails(null, null));
    }
}
