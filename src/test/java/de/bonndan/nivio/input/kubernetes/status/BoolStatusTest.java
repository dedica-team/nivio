package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BoolStatusTest {

    @Test
    void getExtendedStatus() {
        var boolStatus = new BoolStatus();
        var extendedStatus = boolStatus.getExtendedStatus(Map.of("testKey", "testValue"), Mockito.mock(ItemAdapter.class));
        assertThat(extendedStatus).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".boolcondition.testkey", "testvalue"));
        extendedStatus = boolStatus.getExtendedStatus(Map.of("testKey", "testValue"), null);
        assertThat(extendedStatus).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".boolcondition.testkey", "testvalue"));
    }
}