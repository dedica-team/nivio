package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class KPITest {

    @Test
    void sorted() {

        KPI test = new KPI() {
            @Override
            public List<StatusValue> getStatusValues(Component component) {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public Map<Status, RangeApiModel> getRanges() {
                return null;
            }

            @Override
            public Map<Status, List<String>> getMatches() {
                return sorted(
                        Map.of(Status.RED, List.of("foo"), Status.BROWN, List.of("bar", "baz"), Status.GREEN, List.of("hihi"))
                );
            }
        };

        //when
        Map<Status, List<String>> matches = test.getMatches();

        //then
        Iterator<Status> iterator = matches.keySet().iterator();
        assertThat(iterator.next()).isEqualTo(Status.GREEN);
        assertThat(iterator.next()).isEqualTo(Status.RED);
        assertThat(iterator.next()).isEqualTo(Status.BROWN);
    }
}