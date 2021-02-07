package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void doesNotAllowEmptyIdentifier() {
        assertThrows(Exception.class, () -> new Group("", null, null));
    }
}