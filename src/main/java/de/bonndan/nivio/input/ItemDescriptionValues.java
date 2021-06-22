package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Objects;

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

        increment.getRelations().forEach(relationDescription -> {
            RelationDescription rel = relationDescription.findMatching(existing.getRelations())
                    .map(relationDescription1 -> {
                        relationDescription1.merge(relationDescription);
                        return relationDescription1;
                    })
                    .orElse(relationDescription);
            existing.addOrReplaceRelation(rel);
        });

        assignSafe(increment.getInterfaces(), (set) -> set.forEach(intf -> existing.getInterfaces().add(intf)));
    }

    /**
     * Writes the values of the template (second object) to the first where first is null.
     *
     * @param target target
     * @param source source
     */
    public static void assignSafeNotNull(@NonNull final ItemDescription target, @NonNull final ItemDescription source) {

        if (Objects.requireNonNull(source) == Objects.requireNonNull(target)) {
            return;
        }

        ComponentDescriptionValues.assignSafeNotNull(target, source);

        assignSafeIfAbsent(source.getType(), target.getType(), target::setType);
        assignSafeIfAbsent(source.getGroup(), target.getGroup(), target::setGroup);
        assignSafeIfAbsent(source.getIcon(), target.getIcon(), target::setIcon);
        assignSafeIfAbsent(source.getAddress(), target.getAddress(), target::setAddress);

        if (source.getProvidedBy() != null) {
            source.getProvidedBy().stream()
                    .filter(s -> !StringUtils.isEmpty(s) && !target.getProvidedBy().contains(s))
                    .forEach(s -> target.getProvidedBy().add(s));
        }

        source.getRelations().forEach(target::addOrReplaceRelation);

        target.getInterfaces().addAll(source.getInterfaces());
    }
}
