package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.landscape.DataFlowItem;
import de.bonndan.nivio.landscape.Service;

import java.util.ArrayList;
import java.util.List;

class Connections {

    private List<DiagramItem> connections = new ArrayList<>();

    public void add(DataFlowItem flow) {
        connections.add(new DiagramItem().merge("dataflow")
                .set("endpoints", "[" + flow.getSource() + "," + flow.getTarget() + "]"));
    }

    public void add(Service provider, Service service) {
        connections.add(new DiagramItem().merge("provides")
                .set("endpoints", "[" + provider.getIdentifier() + "," + service.getIdentifier() + "]"));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("connections:\n");
        connections.forEach(i -> sb.append("  - " + i.inline()));
        return sb.toString();
    }
}
