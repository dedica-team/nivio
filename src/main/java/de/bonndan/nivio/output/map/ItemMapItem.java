package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.RelationItem;
import de.bonndan.nivio.model.Status;
import de.bonndan.nivio.output.Color;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON representation for custom rendering.
 * <p>
 * The x,y coordinates are derived from the rendered representation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemMapItem extends MapItem {

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

        @JsonSerialize(using = ItemSerializer.class)
        public final Item source;
        @JsonSerialize(using = ItemSerializer.class)
        public final Item target;

        public final String type;
        public final String format;

        public Relation(RelationItem<Item> rel) {
            this.source = rel.getSource();
            this.target = rel.getTarget();
            this.type = rel.getType() != null ? rel.getType().name() : null;
            this.format = rel.getFormat();
        }

        private static class ItemSerializer extends JsonSerializer<Item> {
            @Override
            public void serialize(Item value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.getFullyQualifiedIdentifier().toString());
            }
        }
    }
}
