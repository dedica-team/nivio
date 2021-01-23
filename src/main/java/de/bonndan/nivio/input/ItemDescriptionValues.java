package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.springframework.util.StringUtils;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;
import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

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
        if (increment.getIcon() != null)
            existing.setIcon(increment.getIcon());
        if (increment.getGroup() != null)
            existing.setGroup(increment.getGroup());
        if (increment.getAddress() != null)
            existing.setAddress(increment.getAddress());

        assignSafe(increment.getRelations(), (rel) -> rel.forEach(existing::addRelation));

        assignSafe(increment.getInterfaces(), (set) -> set.forEach(intf -> existing.getInterfaces().add(intf)));
    }

    /**
     * Writes the values of the template (second object) to the first where first is null.
     *
     * @param item     target
     * @param template source
     */
    public static void assignSafeNotNull(ItemDescription item, ItemDescription template) {

        ComponentDescriptionValues.assignSafeNotNull(item, template);

        assignSafeIfAbsent(template.getType(), item.getType(), item::setType);
        assignSafeIfAbsent(template.getGroup(), item.getGroup(), item::setGroup);
        assignSafeIfAbsent(template.getIcon(), item.getIcon(), item::setIcon);
        assignSafeIfAbsent(template.getAddress(), item.getAddress(), item::setAddress);

        if (template.getProvidedBy() != null) {
            template.getProvidedBy().stream()
                    .filter(s -> !StringUtils.isEmpty(s) && !item.getProvidedBy().contains(s))
                    .forEach(s -> item.getProvidedBy().add(s));
        }

        template.getRelations().forEach(item::addRelation);

        item.getInterfaces().addAll(template.getInterfaces());
    }
}
