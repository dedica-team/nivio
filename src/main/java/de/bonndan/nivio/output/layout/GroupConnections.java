package de.bonndan.nivio.output.layout;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

class GroupConnections {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupConnections.class);
    private final List<Pair<String, String>> groupConnections = new ArrayList<>();

    public boolean isConnected(String group) {
        return groupConnections.stream()
                .anyMatch(entry -> entry.getKey().equals(group) || entry.getValue().equals(group));
    }

    public void connect(String a, String b, String message) {
        LOGGER.debug(message + a + " and " + b);
        groupConnections.add(new ImmutablePair(a, b));
    }

    public boolean canConnect(String a, String b) {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            LOGGER.warn("Empty group names in virtual connection check between {} and {}", a, b);
            return false;
        }

        if (a.equals(b))
            return false;

        boolean hasLink = groupConnections.stream()
                .anyMatch(pair -> (pair.getKey().equals(a) && pair.getValue().equals(b)));

        return !hasLink;
    }
}
