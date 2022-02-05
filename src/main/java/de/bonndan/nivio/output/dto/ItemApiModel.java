package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.ServiceInterface;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "fullyQualifiedIdentifier")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemApiModel extends ComponentApiModel {

    @NotNull
    @JsonIgnore
    private final Item item;
    private final String color;

    public ItemApiModel(@NotNull final Item item) {
        this.item = Objects.requireNonNull(item);
        this.color = item.getColor();
        hateoasLinks.putAll(item.getLinks());
    }

    public String getIdentifier() {
        return item.getIdentifier();
    }

    public URI getFullyQualifiedIdentifier() {
        return item.getFullyQualifiedIdentifier();
    }

    public String getName() {
        return item.getName();
    }

    public String getOwner() {
        return item.getOwner();
    }

    public String getIcon() {
        return item.getLabel(Label._icondata);
    }

    public String getColor() {
        return color;
    }

    public String getContact() {
        return item.getContact();
    }

    public String getGroup() {
        return item.getParent().getIdentifier();
    }

    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public Map<String, String> getLabels() {
        return getPublicLabels(item.getLabels());
    }

    @JsonProperty("relations")
    @JsonManagedReference
    public Map<String, RelationApiModel> getJSONRelations() {
        Map<String, RelationApiModel> map = new HashMap<>();

        item.getRelations().forEach(relation -> {
            RelationApiModel apiModel = new RelationApiModel(relation, this.item);
            map.put(this.item.getFullyQualifiedIdentifier().toString(), apiModel);
        });

        return map;
    }

    public String getType() {
        return item.getType();
    }

    public String getAddress() {
        return item.getAddress();
    }

    @JsonManagedReference
    public Set<ServiceInterface> getInterfaces() {
        return item.getInterfaces();
    }

    public String[] getTags() {
        return item.getTags();
    }

    /**
     * @return the fully qualified identifier for this service
     */
    @Override
    public String toString() {
        return getFullyQualifiedIdentifier().toString();
    }

    public String[] getNetworks() {
        return item.getLabels(Label.network).values().toArray(new String[0]);
    }

}
