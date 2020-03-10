package de.bonndan.nivio.model;

/**
 * A component with tags (label key equals value).
 */
public interface Tagged extends Labeled {

    String LABEL_PREFIX_TAG = "tag.";

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
        setLabels(LABEL_PREFIX_TAG, tags, tags);
    }
}
