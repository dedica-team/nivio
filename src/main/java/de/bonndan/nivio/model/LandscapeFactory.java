package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.LandscapeDescription;

public class LandscapeFactory {

    /**
     * Creates a new ladnscape impl.
     *
     * @param input the description
     */
    public static LandscapeImpl toLandscape(LandscapeDescription input) {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier(input.getIdentifier());
        landscape.setSource(input.getSource());
        return landscape;
    }

    public static void assignAll(LandscapeDescription input, LandscapeImpl landscape) {
        landscape.setName(input.getName());
        landscape.setName(input.getName());
        landscape.setContact(input.getContact());
        landscape.setConfig(input.getConfig());
        landscape.setDescription(input.getDescription());
        landscape.setOwner(input.getOwner());
        input.getLabels().forEach((s, s2) -> landscape.getLabels().put(s, s2));
        input.getLinks().forEach(landscape::setLink);
    }
}
