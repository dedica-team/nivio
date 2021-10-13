package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

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
    private final String color;

    @JsonIgnore
    private Map<String, String> labelsToMap;

    public ItemApiModel(@NotNull final Item item, @NotNull final Group group, @NotNull final Map<String, String> labelsToMap) {
        this.item = Objects.requireNonNull(item);

        //ensures that the api model inherits the group colors as fallback
        this.color = !StringUtils.hasLength(item.getColor()) ? group.getColor() : item.getColor();

        this.labelsToMap = labelsToMap;
    }

    public String getIdentifier() {
        return item.getIdentifier();
    }

    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
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
        return item.getGroup();
    }

    public String getDescription() {
        return item.getDescription();
    }

    @Override
    public Map<String, String> getLabels() {
        var frontendMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : getPublicLabels(item.getLabels()).entrySet()) {
            if (labelsToMap.containsKey(entry.getKey())) {
                frontendMap.put(labelsToMap.get(entry.getKey()), entry.getValue());
            } else if (labelsToMap.containsKey(entry.getValue())) {
                frontendMap.put(entry.getKey(), labelsToMap.get(entry.getValue()));
            } else {
                frontendMap.put(entry.getKey(), entry.getValue());
            }
        }
        return frontendMap;
    }

    @JsonProperty("relations")
    @JsonManagedReference
    public Map<String, RelationApiModel> getJSONRelations() {
        Map<String, RelationApiModel> map = new HashMap<>();

        item.getRelations().forEach(relation -> {
            RelationApiModel apiModel = new RelationApiModel(relation, this.item);
            map.put(apiModel.id, apiModel);
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
        if (item.getLandscape() == null) {
            return item.getIdentifier();
        }

        return getFullyQualifiedIdentifier().toString();
    }

}
