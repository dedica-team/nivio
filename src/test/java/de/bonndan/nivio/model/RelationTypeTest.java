package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelationTypeTest {

    @Test
    void filter() {

        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation());

        Relation relation = new Relation();
        relation.setType(RelationType.PROVIDER);
        relationList.add(relation);

        List<Relation> filter = RelationType.PROVIDER.filter(relationList);
        assertNotNull(filter);
        assertEquals(1, filter.size());
        assertEquals(relation, filter.get(0));
    }
}