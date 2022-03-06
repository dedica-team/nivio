package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.ServiceInterface;

import javax.validation.constraints.NotNull;
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

    public ItemApiModel(@NotNull final Item item) {
        super(item);
        this.item = Objects.requireNonNull(item);
        hateoasLinks.putAll(item.getLinks());
    }

    public String getContact() {
        return item.getContact();
    }

    public String getGroup() {
        return item.getParent().getIdentifier();
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

    public String getAddress() {
        return item.getAddress();
    }

    @JsonManagedReference
    public Set<ServiceInterface> getInterfaces() {
        return item.getInterfaces();
    }

    public String[] getNetworks() {
        return item.getLabels(Label.network).values().toArray(new String[0]);
    }

}
