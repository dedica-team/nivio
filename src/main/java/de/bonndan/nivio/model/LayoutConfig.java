package de.bonndan.nivio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

@Schema(description = "Layout configuration for landscapes with unusual number or ratios of items, groups and relations.")
public class LayoutConfig {

    //distance when repulsion has no more effect
    public static final int ITEM_MIN_DISTANCE_LIMIT = 100;
    private static final String ITEM_MIN_DISTANCE_LIMIT_S = "100";
    public static final int ITEM_MAX_DISTANCE_LIMIT = 350;
    private static final String ITEM_MAX_DISTANCE_LIMIT_S = "350";

    public static final int GROUP_MIN_DISTANCE_LIMIT = 50; // Hex.HEX_SIZE / 2
    private static final String GROUP_MIN_DISTANCE_LIMIT_S = "50";
    public static final int GROUP_MAX_DISTANCE_LIMIT = 1000;
    private static final String GROUP_MAX_DISTANCE_LIMIT_S = "1000";

    public static final int ITEM_LAYOUT_INITIAL_TEMP = 300;
    private static final String ITEM_LAYOUT_INITIAL_TEMP_S = "300";

    //results in more iterations and better layouts for larger graphs
    public static final int GROUP_LAYOUT_INITIAL_TEMP = 900;
    private static final String GROUP_LAYOUT_INITIAL_TEMP_S = "900";

    @Schema(description = "A maximum distance between items up to where forces are applied.",
            defaultValue = ITEM_MAX_DISTANCE_LIMIT_S,
            example = ITEM_MAX_DISTANCE_LIMIT_S)
    private Integer itemMaxDistanceLimit;

    @Schema(description = "The minimum distance between items.",
            defaultValue = ITEM_MIN_DISTANCE_LIMIT_S,
            example = ITEM_MIN_DISTANCE_LIMIT_S)
    private Integer itemMinDistanceLimit;

    @Schema(description = "The minimum distance between groups.",
            defaultValue = GROUP_MIN_DISTANCE_LIMIT_S,
            example = GROUP_MIN_DISTANCE_LIMIT_S)
    private Integer groupMinDistanceLimit;

    @Schema(description = "A maximum distance between groups up to where forces are applied.",
            defaultValue = GROUP_MAX_DISTANCE_LIMIT_S,
            example = GROUP_MAX_DISTANCE_LIMIT_S)
    private Integer groupMaxDistanceLimit;

    @Schema(description = "The initial temperature for layouts of items within groups.",
            defaultValue = ITEM_LAYOUT_INITIAL_TEMP_S,
            example = ITEM_LAYOUT_INITIAL_TEMP_S)
    private Integer itemLayoutInitialTemp;

    @Schema(description = "The initial temperature for layouts of groups.",
            defaultValue = GROUP_LAYOUT_INITIAL_TEMP_S,
            example = GROUP_LAYOUT_INITIAL_TEMP_S)
    private Integer groupLayoutInitialTemp;

    @NonNull
    public int getItemMaxDistanceLimit() {
        return itemMaxDistanceLimit == null ? ITEM_MAX_DISTANCE_LIMIT : itemMaxDistanceLimit;
    }

    public void setItemMaxDistanceLimit(Integer itemMaxDistanceLimit) {
        this.itemMaxDistanceLimit = itemMaxDistanceLimit;
    }

    public int getItemMinDistanceLimit() {
        return itemMinDistanceLimit == null ? ITEM_MIN_DISTANCE_LIMIT : itemMinDistanceLimit;
    }

    public void setItemMinDistanceLimit(Integer itemMinDistanceLimit) {
        this.itemMinDistanceLimit = itemMinDistanceLimit;
    }

    public int getGroupMaxDistanceLimit() {
        return groupMaxDistanceLimit == null ? GROUP_MAX_DISTANCE_LIMIT : groupMaxDistanceLimit;
    }

    public void setGroupMaxDistanceLimit(Integer groupMaxDistanceLimit) {
        this.groupMaxDistanceLimit = groupMaxDistanceLimit;
    }

    public int getGroupMinDistanceLimit() {
        return groupMinDistanceLimit == null ? GROUP_MIN_DISTANCE_LIMIT : groupMinDistanceLimit;
    }

    public void setGroupMinDistanceLimit(Integer groupMinDistanceLimit) {
        this.groupMinDistanceLimit = groupMinDistanceLimit;
    }

    public int getItemLayoutInitialTemp() {
        return itemLayoutInitialTemp == null ? ITEM_LAYOUT_INITIAL_TEMP : itemLayoutInitialTemp;
    }

    public void setItemLayoutInitialTemp(Integer itemLayoutInitialTemp) {
        this.itemLayoutInitialTemp = itemLayoutInitialTemp;
    }

    public int getGroupLayoutInitialTemp() {
        return groupLayoutInitialTemp == null ? GROUP_LAYOUT_INITIAL_TEMP : groupLayoutInitialTemp;
    }

    public void setGroupLayoutInitialTemp(Integer groupLayoutInitialTemp) {
        this.groupLayoutInitialTemp = groupLayoutInitialTemp;
    }
}
