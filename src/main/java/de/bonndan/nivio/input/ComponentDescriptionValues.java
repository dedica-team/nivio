package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Labeled;
import org.springframework.util.StringUtils;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;
import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

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
    /**
     * Writes the values of the increment (second object) to the first where first is null/absent.
     *
     * @param item     target
     * @param increment source
     */
    public static void assignSafeNotNull(ComponentDescription item, ComponentDescription increment) {

        assignSafeIfAbsent(increment.getName(), item.getName(), item::setName);
        assignSafeIfAbsent(increment.getDescription(), item.getDescription(), item::setDescription);
        assignSafeIfAbsent(increment.getOwner(), item.getOwner(), item::setOwner);

        Labeled.merge(increment, item);

        increment.getLinks().entrySet().stream()
                .filter(entry -> !item.getLinks().containsKey(entry.getKey()))
                .forEach(entry -> item.getLinks().put(entry.getKey(), entry.getValue()));
    }

}
