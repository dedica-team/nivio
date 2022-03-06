package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.springframework.lang.NonNull;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupApiModel extends ComponentApiModel {

    private final Set<ItemApiModel> items;
    private final String contact;

    public GroupApiModel(@NonNull final Group group, final Set<Item> items) {
        super(group);
        this.contact = group.getContact();
        this.items = items.stream().map(ItemApiModel::new).collect(Collectors.toSet());
    }

    public String getContact() {
        return contact;
    }

    public Set<ItemApiModel> getItems() {
        return items;
    }

}
