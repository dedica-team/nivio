package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.RelationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A hint to new relations or items based on label values.
 *
 * TODO consider to add more item descriptions, e.g. for db server vs db scheme
 * TODO check if the "use(...)" approach is too generic, i.e. classes like DatabaseHint, VolumeHint could be more helpful
 */
class Hint {

    private final String targetType;
    private final RelationType relationType;
    private final String software;
    private final List<ItemDescription> itemDescriptions = new ArrayList<>();

    /**
     * @param targetType   item.type of the target item description
     * @param relationType relation.type of the relation
     * @param software     software name, if known
     */
    public Hint(String targetType, RelationType relationType, String software) {
        this.targetType = targetType;
        this.relationType = relationType;
        this.software = software;
    }

    public Hint() {
        this.targetType = null;
        this.relationType = null;
        this.software = null;
    }

    /**
     * Returns all item descriptions that may have been created or modified
     *
     * @return list of items
     */
    public List<ItemDescription> getCreatedOrModifiedDescriptions() {
        return itemDescriptions;
    }

    /**
     * Applies attributes that are certain to the given items and relations.
     *
     * @param item             the examined object
     * @param target           the other relation end
     * @param existingRelation the existing relation between item and target
     */
    public void use(ItemDescription item, ItemDescription target, Optional<RelationDescription> existingRelation) {

        itemDescriptions.add(item);

        if (software != null) {
            target.setLabel(Label.software, software);
        }

        if (targetType != null) {
            target.setType(targetType);
        }
        itemDescriptions.add(target);

        RelationDescription relationDescription = existingRelation.orElseGet(() -> {
            RelationDescription relation = createRelation(item, target);
            item.addOrReplaceRelation(relation);
            return relation;
        });

        if (relationType != null && relationDescription.getType() == null) {
            relationDescription.setType(relationType);
        }
    }

    /**
     * Creates a new relation description with the correct direction.
     *
     * @param item   item that has the label
     * @param target relation target (or relation source if provider)
     * @return new relation description
     */
    private RelationDescription createRelation(ItemDescription item, ItemDescription target) {
        RelationDescription relationDescription;
        if (relationType == null || relationType != RelationType.PROVIDER) {
            relationDescription = new RelationDescription(item.getIdentifier(), target.getIdentifier());
        } else {
            relationDescription = new RelationDescription(target.getIdentifier(), item.getIdentifier());
        }

        Optional.ofNullable(relationType).ifPresent(relationDescription::setType);

        return relationDescription;
    }
}
