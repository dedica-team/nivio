package de.bonndan.nivio.output.map;

import java.util.ArrayList;
import java.util.List;

public class RenderedXYMap {

    public final List<ItemMapItem> items = new ArrayList<>();
    public final List<GroupMapItem> groups = new ArrayList<>();

    public Integer width;
    public Integer height;

    public Integer minQ;
    public Integer maxQ;
    public Integer minR;
    public Integer maxR;
}
