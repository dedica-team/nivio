package de.bonndan.nivio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
    private Integer itemMaxDistanceLimit = ITEM_MAX_DISTANCE_LIMIT;

    @Schema(description = "The minimum distance between items.",
            defaultValue = ITEM_MIN_DISTANCE_LIMIT_S,
            example = ITEM_MIN_DISTANCE_LIMIT_S)
    private Integer itemMinDistanceLimit = ITEM_MIN_DISTANCE_LIMIT;

    @Schema(description = "The minimum distance between groups.",
            defaultValue = GROUP_MIN_DISTANCE_LIMIT_S,
            example = GROUP_MIN_DISTANCE_LIMIT_S)
    private Integer groupMinDistanceLimit = GROUP_MIN_DISTANCE_LIMIT;

    @Schema(description = "A maximum distance between groups up to where forces are applied.",
            defaultValue = GROUP_MAX_DISTANCE_LIMIT_S,
            example = GROUP_MAX_DISTANCE_LIMIT_S)
    private Integer groupMaxDistanceLimit = GROUP_MAX_DISTANCE_LIMIT;

    @Schema(description = "The initial temperature for layouts of items within groups.",
            defaultValue = ITEM_LAYOUT_INITIAL_TEMP_S,
            example = ITEM_LAYOUT_INITIAL_TEMP_S)
    private Integer itemLayoutInitialTemp = ITEM_LAYOUT_INITIAL_TEMP;

    @Schema(description = "The initial temperature for layouts of groups.",
            defaultValue = GROUP_LAYOUT_INITIAL_TEMP_S,
            example = GROUP_LAYOUT_INITIAL_TEMP_S)
    private Integer groupLayoutInitialTemp = GROUP_LAYOUT_INITIAL_TEMP;

    @NonNull
    public int getItemMaxDistanceLimit() {
        return itemMaxDistanceLimit;
    }

    public void setItemMaxDistanceLimit(@Nullable final Integer itemMaxDistanceLimit) {
        if (itemMaxDistanceLimit != null) {
            this.itemMaxDistanceLimit = itemMaxDistanceLimit;
        }
    }

    @NonNull
    public int getItemMinDistanceLimit() {
        return itemMinDistanceLimit;
    }

    public void setItemMinDistanceLimit(@Nullable final Integer itemMinDistanceLimit) {
        if (itemMinDistanceLimit != null) {
            this.itemMinDistanceLimit = itemMinDistanceLimit;
        }
    }

    @NonNull
    public int getGroupMaxDistanceLimit() {
        return groupMaxDistanceLimit;
    }

    public void setGroupMaxDistanceLimit(@Nullable final Integer groupMaxDistanceLimit) {
        if (groupMaxDistanceLimit != null) {
            this.groupMaxDistanceLimit = groupMaxDistanceLimit;
        }
    }

    @NonNull
    public int getGroupMinDistanceLimit() {
        return groupMinDistanceLimit;
    }

    public void setGroupMinDistanceLimit(@Nullable final Integer groupMinDistanceLimit) {
        if (groupMinDistanceLimit != null) {
            this.groupMinDistanceLimit = groupMinDistanceLimit;
        }
    }

    @NonNull
    public int getItemLayoutInitialTemp() {
        return itemLayoutInitialTemp;
    }

    public void setItemLayoutInitialTemp(@Nullable final Integer itemLayoutInitialTemp) {
        if (itemLayoutInitialTemp != null) {
            this.itemLayoutInitialTemp = itemLayoutInitialTemp;
        }
    }

    @NonNull
    public int getGroupLayoutInitialTemp() {
        return groupLayoutInitialTemp;
    }

    public void setGroupLayoutInitialTemp(@Nullable final Integer groupLayoutInitialTemp) {
        if (groupLayoutInitialTemp != null) {
            this.groupLayoutInitialTemp = groupLayoutInitialTemp;
        }
    }
}
