package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelationTypeTest {

    @Test
    void filter() {

        List<RelationItem> relationList = new ArrayList<>();
        relationList.add(new Relation());

        Relation relation = new Relation();
        relation.setType(RelationType.PROVIDER);
        relationList.add(relation);

        List<RelationItem> filter = RelationType.PROVIDER.filter(relationList);
        assertNotNull(filter);
        assertEquals(1, filter.size());
        assertEquals(relation, filter.get(0));
    }
}