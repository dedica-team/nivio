package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.RelationType;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

class Hint {

    private static final Map<String, Supplier<Hint>> uriHints = new HashMap<>();

    static {
        uriHints.put("mysql", () -> hintsTo(ItemType.DATABASE));
        uriHints.put("mongodb", () -> hintsTo(ItemType.DATABASE));
        uriHints.put("http", () -> hintsTo(RelationType.DATAFLOW));
        uriHints.put("https", () -> hintsTo(RelationType.DATAFLOW));
        uriHints.put("jdbc", () -> hintsTo(ItemType.DATABASE));
        uriHints.put("redis", () -> hintsTo(ItemType.KEYVALUESTORE));
        uriHints.put("rediss", () -> hintsTo(ItemType.KEYVALUESTORE));
        uriHints.put("smb", () -> hintsTo(ItemType.VOLUME));
    }

    String targetType;
    RelationType relationType;
    private URI uri;
    private String label;

    public static Hint hintsTo(String targetType) {
        return new Hint(targetType, null);
    }

    public static Hint hintsTo(RelationType relationType) {
        return new Hint(null, relationType);
    }

    public Hint(String targetType, RelationType relationType) {
        this.targetType = targetType;
        this.relationType = relationType;
    }

    public Hint() {
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    public void extendLandscape(Landscape landscape, ProcessLog processLog) {
        //search for targets in the landscape, i.e. where name or identifier of an item matches the "possible targets"
        labelMatch.possibleTargets.forEach(toFind -> {
            String s = landscape.getItems().selectByIdentifierOrName(toFind);
            Collection<? extends Item> possibleTargets = landscape.getItems().cqnQueryOnIndex(s);

            if (possibleTargets.size() != 1) {
                processLog.debug("Found no target of magic relation from item " + item.getIdentifier() + " using '" + toFind + "'");
                return;
            }

            String source = item.getIdentifier();
            String target = possibleTargets.iterator().next().getIdentifier();
            processLog.info("Found a target of magic relation from "
                    + item.getIdentifier() + "(" + item.getName() + ")"
                    + " to target '" + target + "' using '" + toFind + "'");
            boolean relationExists = item.getRelations().stream()
                    .anyMatch(r -> hasRelation(source, target, r));
            boolean isEqual = source.equalsIgnoreCase(target);
            if (!relationExists && !isEqual) {
                RelationDescription relation = new RelationDescription(source, target);
                //inverse
                if (isProvider(labelMatch)) {
                    relation = new RelationDescription(target, source);
                    relation.setType(RelationType.PROVIDER);
                }
                item.addRelation(relation);
                return;
            }

            processLog.debug("Relation between " + source + " and " + target + " already exists, not adding magic one.");
        });
    }

    private boolean hasRelation(String source, String target, RelationDescription r) {
        return r.getSource().equals(source) && r.getTarget().equals(target) ||
                r.getSource().equals(target) && r.getTarget().equals(source);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
