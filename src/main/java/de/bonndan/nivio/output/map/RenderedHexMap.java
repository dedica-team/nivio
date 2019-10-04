package de.bonndan.nivio.output.map;

import java.util.ArrayList;
import java.util.List;

public class RenderedHexMap {

    public final List<HexMapItem> items = new ArrayList<>();
    public final List<HexMapItem> groups = new ArrayList<>();

    public Integer minQ;
    public Integer maxQ;
    public Integer minR;
    public Integer maxR;
}
