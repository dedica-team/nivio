package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.model.Labeled;

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

        if (increment.getContact() != null)
            existing.setContact(increment.getContact());

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
     * @param component     target
     * @param increment source
     */
    public static void assignSafeNotNull(ComponentDescription component, ComponentDescription increment) {

        assignSafeIfAbsent(increment.getName(), component.getName(), component::setName);

        assignSafeIfAbsent(increment.getDescription(), component.getDescription(), component::setDescription);
        assignSafeIfAbsent(increment.getOwner(), component.getOwner(), component::setOwner);
        assignSafeIfAbsent(increment.getContact(), component.getContact(), component::setContact);

        Labeled.add(increment, component);

        increment.getLinks().entrySet().stream()
                .filter(entry -> !component.getLinks().containsKey(entry.getKey()))
                .forEach(entry -> component.getLinks().put(entry.getKey(), entry.getValue()));
    }

}
