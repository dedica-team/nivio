package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultDetailsTest {
    DefaultDetails defaultDetails = new DefaultDetails();

    @Test
    void testGetExtendedDetails() {
        ItemAdapter itemAdapter = Mockito.mock(ItemAdapter.class);
        Mockito.when(itemAdapter.getName()).thenReturn("name");
        Mockito.when(itemAdapter.getNamespace()).thenReturn("namespace");
        Mockito.when(itemAdapter.getCreationTimestamp()).thenReturn("creationTimestamp");
        Map<String, String> result = defaultDetails.getExtendedDetails(new HashMap<>() {{
            put("String", "String");
        }}, itemAdapter);
        assertThat(result).isEqualTo(Map.of("name", "name", "namespace", "namespace", "creation", "creationTimestamp"));
    }

    @Test
    void testGetExtendedDetailsException() {
        assertThrows(NullPointerException.class, () -> defaultDetails.getExtendedDetails(null, null));
    }
}
