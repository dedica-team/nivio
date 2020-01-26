package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.Color;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON representation for custom rendering.
 * <p>
 * The x,y coordinates are derived from the rendered representation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public
class ItemMapItem extends MapItem {

    public long x;
    public long y;
    public double width;
    public double height;
    public final String status;
    public final String group;
    public final LandscapeItem landscapeItem;
    public final List<Relation> relations = new ArrayList<>();

    public ItemMapItem(Item item, String image, long x, long y, long width, long height) {
        super(item.getFullyQualifiedIdentifier().toString(), StringUtils.isEmpty(item.getName()) ? item.getIdentifier() : item.getName(), image, "item", Color.getGroupColor(item));

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.group = item.getGroup();
        this.landscapeItem = item;
        this.status = Status.highestOf(item.getStatuses()).toString();
        List<Relation> collect = item.getRelations().stream()
                .filter(rel -> rel.getSource().equals(item))
                .map(Relation::new)
                .collect(Collectors.toList());
        relations.addAll(collect);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Relation {
        public final String target;
        public final String type;
        public final String format;

        public Relation(RelationItem<Item> rel) {
            this.target = rel.getTarget().getFullyQualifiedIdentifier().toString();
            this.type = rel.getType() != null ? rel.getType().name() : null;
            this.format = rel.getFormat();
        }
    }
}
