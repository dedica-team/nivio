package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.model.Item;

public class Icon extends DiagramItem {

    private final Item item;

    public Icon (Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
