package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.bonndan.nivio.input.demo.PetClinicSimulatorResolver.LANDSCAPE_IDENTIFIER_PETCLINIC;
import static de.bonndan.nivio.input.demo.PetClinicSimulatorResolver.RADIATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PetClinicSimulatorResolverTest {

    private PetClinicSimulatorResolver resolver;
    private LandscapeDescription input;

    @BeforeEach
    void setup() {
        input = new LandscapeDescription(LANDSCAPE_IDENTIFIER_PETCLINIC);
        ItemDescription sensor = new ItemDescription("sensor");
        sensor.setGroup("xray");
        input.getItemDescriptions().add(sensor);
        ItemDescription customerDB = new ItemDescription("customer-db");
        customerDB.setGroup("xray");
        input.getItemDescriptions().add(customerDB);
        resolver = new PetClinicSimulatorResolver(mock(ProcessLog.class));
    }

    @Test
    void simulatesChangingRadiation() {

        ItemDescription sensor = input.getItemDescriptions().pick("sensor", "xray");
        int currentRad = 0;
        sensor.setLabel(RADIATION, String.valueOf(currentRad));

        //when
        resolver.resolve(input);

        //then
        @SuppressWarnings("ConstantConditions") int i = Integer.parseInt(sensor.getLabel(RADIATION));
        assertThat(i).isNotEqualTo(currentRad);
    }
}