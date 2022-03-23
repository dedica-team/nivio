package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.search.SearchDocumentValueObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SearchDocumentValueObjectFactoryTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    void createForItem() {

        Item item = new Item("anId",
                "aName",
                "anOwner",
                "aContact",
                "aDescription",
                "00ffaa",
                null,
                ItemType.DATABASE,
                URI.create("http://acme.com"),
                Layer.domain,
                graph.groupA
        );
        item.setTags(List.of("foo", "bar").toArray(String[]::new));

        graph.landscape.getWriteAccess().addOrReplaceChild(item);

        //when
        SearchDocumentValueObject valueObject = SearchDocumentValueObjectFactory.createFor(item);

        //then
        assertThat(valueObject).isNotNull();
        assertThat(valueObject.getIdentifier()).isEqualTo(item.getIdentifier());
        assertThat(valueObject.getOwner()).isEqualTo(item.getOwner());
        assertThat(valueObject.getDescription()).isEqualTo(item.getDescription());
        assertThat(valueObject.getType()).isEqualTo(item.getType());
        assertThat(valueObject.getTags()).isEqualTo(item.getTags());
        assertThat(valueObject.getGroup()).isPresent().get().isEqualTo(item.getParentIdentifier());
        assertThat(valueObject.getLayer()).isPresent().get().isEqualTo(item.getLayer());
        assertThat(valueObject.getComponentClass()).isEqualTo(ComponentClass.item);
    }

    @Test
    void createForChildRelationIsNull() {

        Optional<Relation> first = graph.landscape.getReadAccess().all(Relation.class).stream().findFirst();

        //when
        SearchDocumentValueObject valueObject = SearchDocumentValueObjectFactory.createFor(first.get());

        //then
        assertThat(valueObject).isNull();
    }

    @Test
    void createForRelation() {

        Relation relation = RelationFactory.create(graph.itemAA, graph.itemAC);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        //when
        SearchDocumentValueObject valueObject = SearchDocumentValueObjectFactory.createFor(relation);

        //then
        assertThat(valueObject).isNotNull();
        assertThat(valueObject.getComponentClass()).isEqualTo(ComponentClass.relation);
    }
}