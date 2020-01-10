package de.bonndan.nivio.output.map;

import java.util.ArrayList;
import java.util.List;

public class RenderedXYMap {

    public final List<ItemMapItem> items = new ArrayList<>();
    public final List<GroupMapItem> groups = new ArrayList<>();

    public String landscape;
    public Integer width;
    public Integer height;

    /**
     * proportional scaling of coordinates
     */
    public int sizeFactor;
}
