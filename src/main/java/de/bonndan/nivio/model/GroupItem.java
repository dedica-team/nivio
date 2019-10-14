package de.bonndan.nivio.model;

import java.util.Set;

public interface GroupItem {

    String getIdentifier();

    String getOwner();

    String getTeam();

    String getDescription();

    String getContact();

    String getColor();

    Set<LandscapeItem> getItems();
}
