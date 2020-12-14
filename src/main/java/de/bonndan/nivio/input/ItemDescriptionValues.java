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

        ComponentDescriptionValues.assignNotNull(existing, increment);

        if (increment.getType() != null)
            existing.setType(increment.getType());
        if (increment.getContact() != null)
            existing.setContact(increment.getContact());

        if (increment.getGroup() != null)
            existing.setGroup(increment.getGroup());

        assignSafe(increment.getRelations(), (rel) -> rel.forEach(existing::addRelation));

        assignSafe(increment.getInterfaces(), (set) -> set.forEach(intf -> existing.getInterfaces().add(intf)));
    }
}
