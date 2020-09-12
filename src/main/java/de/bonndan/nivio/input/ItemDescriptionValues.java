package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

public class ItemDescriptionValues {
    /**
     * Overwrites and fields on the existing with values of the increment unless the increment value is null.
     *
     * @param existing  current description present in the landscape
     * @param increment new values
     */
    public static void assignNotNull(ItemDescription existing, ItemDescription increment) {

        if (increment.getName() != null)
            existing.setName(increment.getName());
        if (increment.getType() != null)
            existing.setType(increment.getType());
        if (increment.getDescription() != null)
            existing.setDescription(increment.getDescription());
        if (increment.getContact() != null)
            existing.setContact(increment.getContact());

        if (increment.getOwner() != null)
            existing.setOwner(increment.getOwner());
        if (increment.getGroup() != null)
            existing.setGroup(increment.getGroup());

        if (increment.getLabels() != null) {
            increment.getLabels().forEach((s, s2) -> {
                if (increment.getLabel(s) != null) {
                    existing.setLabel(s, s2);
                }
            });
        }

        existing.getLinks().putAll(increment.getLinks());

        assignSafe(increment.getRelations(), (rel) -> rel.forEach(existing::addRelation));

        assignSafe(increment.getInterfaces(), (set) -> set.forEach(intf -> existing.getInterfaces().add(intf)));
    }
}
