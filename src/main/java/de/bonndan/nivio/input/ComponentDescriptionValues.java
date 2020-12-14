package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

public class ComponentDescriptionValues {

    /**
     * Overwrites labels and links.
     *
     * @param existing  current description present in the landscape
     * @param increment new values
     */
    public static void assignNotNull(ComponentDescription existing, ComponentDescription increment) {

        if (increment.getName() != null)
            existing.setName(increment.getName());
        if (increment.getDescription() != null)
            existing.setDescription(increment.getDescription());

        if (increment.getOwner() != null)
            existing.setOwner(increment.getOwner());

        if (increment.getLabels() != null) {
            increment.getLabels().forEach((s, s2) -> {
                if (increment.getLabel(s) != null) {
                    existing.setLabel(s, s2);
                }
            });
        }

        existing.getLinks().putAll(increment.getLinks());

    }
}
