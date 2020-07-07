package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.RelationItem;

import java.util.ArrayList;
import java.util.List;

class Connections {

    private final List<DiagramItem> connections = new ArrayList<>();

    void addDataflow(RelationItem flow) {
        connections.add(new DiagramItem().merge("dataflow")
                .set("endpoints", "[" + flow.getSource() + "," + flow.getTarget() + "]"));
    }

    void addProvider(String provider, Item item) {
        connections.add(new DiagramItem().merge("provides")
                .set("endpoints", "[" + provider + "," + item.getIdentifier() + "]"));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("connections:\n");
        connections.forEach(i -> sb.append("  - ").append(i.inline()));
        return sb.toString();
    }
}
