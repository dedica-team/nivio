package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.search.NullSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GraphComponentTest {

    private Index<GraphComponent> index;
    private Context component;
    private Unit parent;

    @BeforeEach
    void setup() {
        index = new Index<>(new NullSearchIndex());
        var graph = new GraphTestSupport();
        parent = graph.unit;
        component = new Context("test", "aName", "aOwner", "aContent", "aDescription", "aType", graph.unit);
    }

    @Test
    void doesNotAllowEmptyIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> new Context(null, null, null, null, null, null, parent));
    }

    @Test
    void doesNotAllowMissingParentURI() {
        assertThrows(NullPointerException.class, () -> new Context("foo", null, null, null, null, null, null));
    }

    @Test
    void setAllFields() {
        assertThat(component.getIdentifier()).isNotEmpty();
        assertThat(component.getName()).isNotEmpty();
        assertThat(component.getOwner()).isNotEmpty();
        assertThat(component.getContact()).isNotEmpty();
        assertThat(component.getDescription()).isNotEmpty();
        assertThat(component.getType()).isNotEmpty();
    }

    @Test
    void attach() {
        index = mock(Index.class);
        component.attach(new IndexReadAccess<>(index));

        //when
        assertThrows(NoSuchElementException.class, () -> component.getParent());
        verify(index).get(parent.getFullyQualifiedIdentifier());
    }

    @Test
    void hasNoChanges() {

        //given
        var newer = component;

        //when
        List<String> changes = component.getChanges(newer);
        assertThat(changes).isEmpty();
    }

    @Test
    void hasChanges() {
        var newer = new Context("test", "foo", "bar", "aContent", "aDescription", "aType", parent);

        //when
        List<String> changes = component.getChanges(newer);
        assertThat(changes).hasSize(2);
    }

    @Test
    void getChildrenIsSorted() {

        //given
        component.attach(new IndexReadAccess<>(index));

        //then
        assertThat(component.getChildren()).isInstanceOf(LinkedHashSet.class);
    }
}