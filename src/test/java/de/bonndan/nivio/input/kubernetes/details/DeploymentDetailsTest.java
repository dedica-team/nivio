package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeploymentDetailsTest {
    DeploymentDetails deploymentDetails;
    DeploymentItemAdapter itemAdapter;

    @BeforeEach
    void setUp() {
        itemAdapter = Mockito.mock(DeploymentItemAdapter.class);
        Mockito.when(itemAdapter.getCreationTimestamp()).thenReturn("creationTimestamp");
        Mockito.when(itemAdapter.getStrategyType()).thenReturn("strategyType");
        Mockito.when(itemAdapter.getNamespace()).thenReturn("namespace");
        Mockito.when(itemAdapter.getName()).thenReturn("name");
    }

    @Test
    void testGetExtendedDetails() {
        deploymentDetails = new DeploymentDetails(new DefaultDetails());
        Map<String, String> result = deploymentDetails.getExtendedDetails(Map.of(), itemAdapter);
        assertThat(result).isEqualTo(Map.of("name", "name", "namespace", "namespace", "strategy", "strategyType", "creation", "creationTimestamp"));
    }

    @Test
    void testGetExtendedDetailsClassCastException() {
        ServiceDetails serviceDetails = new ServiceDetails(new DeploymentDetails(new DefaultDetails()));
        Map<String, String> result = serviceDetails.getExtendedDetails(Map.of(), itemAdapter);
        assertThat(result).isEqualTo(Map.of("name", "name", "namespace", "namespace", "strategy", "strategyType", "creation", "creationTimestamp"));
    }


    @Test
    void testGetExtendedDetailsException() {
        assertThrows(NullPointerException.class, () -> deploymentDetails.getExtendedDetails(null, null));
    }

}