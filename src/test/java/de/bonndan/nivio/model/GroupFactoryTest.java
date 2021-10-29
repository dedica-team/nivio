package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GroupFactoryTest {


    @Test
    void testMerge() {
        Group one = new Group("a", "test", "Joe", "a", "mail", "#123123");

        Group two = new Group("a", "test", "Matt", "a", "mail", null);

        Group merged = GroupFactory.merge(one, two);

        assertEquals("Joe", merged.getOwner());
        assertEquals("a", merged.getDescription());
        assertEquals("mail", merged.getContact());
        assertEquals("#123123", merged.getColor());
    }

    @Test
    void usesExistingValues() {
        Group in = new Group("a", "foo", "Matt", "mail", "one", "00ffee");

        Group merge = GroupFactory.merge(in, null);
        assertThat(merge).isNotNull();
        assertThat(merge.getIdentifier()).isEqualTo(in.getIdentifier());
        assertThat(merge.getDescription()).isEqualTo(in.getDescription());
        assertThat(merge.getContact()).isEqualTo(in.getContact());
        assertThat(merge.getColor()).isEqualTo(in.getColor());
        assertThat(merge.getIcon()).isEqualTo(in.getIcon());
        assertThat(merge.getLinks()).isEqualTo(in.getLinks());
        assertThat(merge.getLabels()).isEqualTo(in.getLabels());
    }

    @Test
    void mergeSetsDefaultColor() {
        Group one = new Group("a", "test", "Matt", "mail", null, null);
        Group two = new Group("a", "test", "Matt", "mail", null, null);

        Group merge = GroupFactory.merge(one, two);
        assertThat(merge).isNotNull();
        assertThat(merge.getColor()).isNotEmpty();
    }
}
