package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.input.Resolver;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Simulates a pet clinic landscape with changing values.
 */
public class PetClinicSimulatorResolver implements Resolver {

    public static final String LANDSCAPE_IDENTIFIER_PETCLINIC = "petclinic";
    public static final String RADIATION = "radiation";

    @NonNull
    @Override
    public LandscapeDescription resolve(@NonNull final LandscapeDescription input) {
        if (!input.getIdentifier().equals(LANDSCAPE_IDENTIFIER_PETCLINIC)) {
            return input;
        }

        simulateHealth(input);
        simulateRadiation(input);

        //no need to refresh, structure is not changed
        return input;
    }

    private void simulateHealth(LandscapeDescription input) {
        List<ItemDescription> all = new ArrayList<>(input.getReadAccess().all(ItemDescription.class));

        ItemDescription picked = all.stream()
                .filter(itemDescription -> Optional.ofNullable(itemDescription.getLabel(Label.health)).map(s -> s.equals(HealthKPI.UNHEALTHY)).orElse(false))
                .findFirst()
                .orElseGet(() -> {
                    Collections.shuffle(all);
                    return Optional.ofNullable(all.get(0)).orElse(null);
                });

        String health = Optional.ofNullable(picked.getLabel(Label.health)).orElse(HealthKPI.HEALTHY);
        picked.setLabel(Label.health, health.equals(HealthKPI.UNHEALTHY) ? HealthKPI.HEALTHY : HealthKPI.UNHEALTHY);
        input.getProcessLog().info(String.format("Simulating health %s on item %s", picked.getLabel(Label.health), picked));
    }

    private void simulateRadiation(LandscapeDescription input) {
        Random r = new Random();
        int low = 1;
        int high = 1000;
        int mrem = r.nextInt(high - low) + low;
        ItemDescription sensor;
        try {
            sensor = input.getReadAccess().matchOneByIdentifiers("sensor", "xray", ItemDescription.class).orElseThrow();
        } catch (Exception e) {
            input.getProcessLog().warn("Failed to run simulation: " + e.getMessage());
            return;
        }

        int currentRad = Integer.parseInt(Optional.ofNullable(sensor.getLabel(RADIATION)).orElse("0"));
        if (mrem == currentRad) {
            mrem--;
        }
        input.getProcessLog().info(String.format("Simulating radiation of %d mrem on item %s", mrem, sensor));
        sensor.setLabel(RADIATION.toLowerCase(Locale.ROOT), String.valueOf(mrem));

        try {

        ItemDescription customerDb = input.getReadAccess().matchOneByIdentifiers("customer-db", "xray", ItemDescription.class).orElseThrow();
        int scale = mrem > 300 ? 0 : 1;
        customerDb.setLabel(Label.scale, String.valueOf(scale));
            input.getProcessLog().info(String.format("Customer DB is scaled to %s", scale));
        } catch (Exception e) {
            input.getProcessLog().warn("Failed to run simulation: " + e.getMessage());
        }
    }

}
