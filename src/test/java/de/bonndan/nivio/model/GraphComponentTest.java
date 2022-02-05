package de.bonndan.nivio.model;

import de.bonndan.nivio.search.NullSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GraphComponentTest {

    private Index<GraphComponent> index;
    private TestGraphComponent component;
    private URI parent;

    @BeforeEach
    void setup() {
        index = new Index<>(new NullSearchIndex());
        parent = URI.create("item://foo/bar/baz");
        component = new TestGraphComponent("test", "aName", "aOwner", "aContent", "aDescription", "aType", parent);
    }

    @Test
    void doesNotAllowEmptyIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> new TestGraphComponent(null, null, null, null, null, null, parent));
    }

    @Test
    void doesNotAllowMissingParentURI() {
        assertThrows(NullPointerException.class, () -> new TestGraphComponent("foo", null, null, null, null, null, null));
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
        verify(index).get(parent);
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
        var newer = new TestGraphComponent("test", "foo", "bar", "aContent", "aDescription", "aType", parent);

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

    static class TestGraphComponent extends GraphComponent {

        protected TestGraphComponent(String identifier, String name, String owner, String contact, String description, String type, URI parent) {
            super(identifier, name, owner, contact, description, type, parent);
        }

        @Override
        public GraphComponent getParent() {
            return _getParent(GraphComponent.class);
        }

        @NonNull
        @Override
        public Set<? extends GraphComponent> getChildren() {
            return super.getChildren(component -> true, GraphComponent.class);
        }
    }
}