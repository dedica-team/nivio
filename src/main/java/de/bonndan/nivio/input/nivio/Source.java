package de.bonndan.nivio.input.nivio;

import de.bonndan.nivio.input.dto.ContextDescription;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.UnitDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents source file content with its sections.
 */
public class Source {

    public Map<String, UnitDescription> units = new HashMap<>();
    public Map<String, ContextDescription> contexts = new HashMap<>();
    public Map<String, GroupDescription> groups = new HashMap<>();
    public List<ItemDescription> items = new ArrayList<>();

    public Map<String, ItemDescription> templates = new HashMap<>();
}
