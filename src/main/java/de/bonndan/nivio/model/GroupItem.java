package de.bonndan.nivio.model;

import java.net.URL;
import java.util.Map;

public interface GroupItem {

    String getIdentifier();

    String getOwner();

    String getTeam();

    String getDescription();

    String getContact();

    String getColor();

    Map<String, URL> getLinks();
}
