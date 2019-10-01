package de.bonndan.nivio.output.graphstream;

import de.bonndan.nivio.model.Groups;
import de.bonndan.nivio.model.Item;
import org.graphstream.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Positioner {

    private static Logger logger = LoggerFactory.getLogger(Positioner.class);

    private Map<Item, Node> pairs = new HashMap<>();

    private Groups groups = new Groups();

    public void add(Item item, Node node) {
        pairs.put(item, node);
        groups.add(item.getGroup(), item);
    }

    public void compute () {
        groups.getAll().forEach((group, services) -> {
            int width = services.size();
            AtomicInteger i = new AtomicInteger(0);
            services.forEach(service -> {
                Node n = pairs.getOrDefault(service, null);
                position(n, i.getAndIncrement());
            });
        });
    }

    private void position(Node n, int count) {
        n.setAttribute("x", 50 * count);
        n.setAttribute("y", 10);
        //n.setAttribute("layout.frozen", true);
        //logger.info("Positioned " + n.getId() + " at x " + count);
    }
}
