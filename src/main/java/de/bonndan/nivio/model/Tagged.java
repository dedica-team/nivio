package de.bonndan.nivio.model;

import org.springframework.util.StringUtils;

/**
 * A component with tags (label key equals value).
 */
public interface Tagged extends Labeled {

    String LABEL_PREFIX_TAG = Label.INTERNAL_LABEL_PREFIX + "tag.";

    /**
     * Returns a copy of the components tags.
     *
     * @return tag list
     */
    default String[] getTags() {
        return getLabels(LABEL_PREFIX_TAG).values().toArray(new String[0]);
    }

    /**
     * Adds all tags.
     *
     * @param tags tags to add
     */
    default void setTags(String[] tags) {

        for (String tag : tags) {
            if (StringUtils.hasLength(tag)) {
                tag = tag.toLowerCase();
                setLabel(LABEL_PREFIX_TAG + tag, tag);
            }
        }
    }
}
