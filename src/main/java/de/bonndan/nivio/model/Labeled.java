package de.bonndan.nivio.model;

/**
 * Anything that has labels (key-value).
 *
 *
 */
public interface Labeled {

    String getLabel(String key);

    void setLabel(String key, String value);
}
