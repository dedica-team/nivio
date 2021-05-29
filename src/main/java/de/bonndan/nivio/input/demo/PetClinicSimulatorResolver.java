package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.Resolver;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;

import java.util.*;

/**
 * Simulates a pet clinic landscape with changing values.
 */
public class PetClinicSimulatorResolver extends Resolver {

    public PetClinicSimulatorResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription input) {
        if (!input.getIdentifier().equals("petclinic")) {
            return;
        }

        simulateScale(input);
        simulateRadiation(input);
    }

    private void simulateScale(LandscapeDescription input) {
        List<ItemDescription> all = new ArrayList<>(input.getItemDescriptions().all());
        Collections.shuffle(all);
        Optional.ofNullable(all.get(0)).ifPresent(itemDescription -> {
            int scale = 1;
            try {
                scale = Integer.parseInt(itemDescription.getLabel(Label.scale));
            } catch (NumberFormatException ignored) {
                //the item had no or an invalid scale label, which is ok for the simulation
            }
            if (scale <= 0) {
                itemDescription.setLabel(Label.scale, "1");
            } else {
                itemDescription.setLabel(Label.scale, "0");
            }
            processLog.info(String.format("Simulating scale %s on item %s", itemDescription.getLabel(Label.scale), itemDescription));
        });
    }

    private void simulateRadiation(LandscapeDescription input) {
        Random r = new Random();
        int low = 0;
        int high = 1000;
        int mrem = r.nextInt(high - low) + low;

        ItemDescription sensor = input.getItemDescriptions().pick("sensor", "xray");
        processLog.info(String.format("Simulating radiation of %d mrem on item %s", mrem, sensor));
        sensor.setLabel("radiation", mrem);
    }

}
