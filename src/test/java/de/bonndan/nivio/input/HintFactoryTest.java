package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HintFactoryTest {

    private HintFactory hintFactory;
    private LandscapeDescription landscapeDescription;
    private ItemDescription one;

    @BeforeEach
    void setUp() {
        landscapeDescription = new LandscapeDescription("landscape");
        one = new ItemDescription();
        one.setIdentifier("one");
        one.setGroup("foo");
        landscapeDescription.mergeItems(List.of(one));

        hintFactory = new HintFactory();
    }

    @Test
    void createWithMysqlURI() {

        //given
        one.setLabel("foo", "mysql://somehost/abc");

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "foo");

        //then
        assertThat(foo).isNotEmpty();
        List<ItemDescription> createdOrModifiedDescriptions = foo.get().getCreatedOrModifiedDescriptions();
        assertThat(createdOrModifiedDescriptions.size()).isEqualTo(2);

        ItemDescription created = createdOrModifiedDescriptions.get(1);
        assertThat(created.getType()).isEqualTo(ItemType.DATABASE);
        assertThat(created.getGroup()).isEqualTo(one.getGroup());
        assertThat(created.getIdentifier()).isEqualTo("somehost_abc");

        RelationDescription next = one.getRelations().iterator().next();
        assertThat(next.getType()).isEqualTo(RelationType.PROVIDER);
    }

    @Test
    void createWithHttpURI() {

        //given
        one.setLabel("foo", "http://foo.bar.baz");

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "foo");

        //then
        assertThat(foo).isNotEmpty();
        List<ItemDescription> createdOrModifiedDescriptions = foo.get().getCreatedOrModifiedDescriptions();
        assertThat(createdOrModifiedDescriptions.size()).isEqualTo(2);

        ItemDescription created = createdOrModifiedDescriptions.get(1);
        assertThat(created.getIdentifier()).isEqualTo("foo.bar.baz");

        RelationDescription next = one.getRelations().iterator().next();
        assertThat(next.getType()).isEqualTo(RelationType.DATAFLOW);
    }

    @Test
    @DisplayName("does not link same service to itself")
    public void linksByIdentifier() {
        //given
        ItemDescription hihi = new ItemDescription();
        hihi.setIdentifier("something");
        landscapeDescription.mergeItems(List.of(hihi));

        one.getLabels().put("BASE_URL", hihi.getIdentifier());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "BASE_URL");

        //then
        assertThat(foo).isNotEmpty();
        List<ItemDescription> createdOrModifiedDescriptions = foo.get().getCreatedOrModifiedDescriptions();
        assertThat(createdOrModifiedDescriptions.size()).isEqualTo(2);

        ItemDescription created = createdOrModifiedDescriptions.get(1);
        assertThat(created).isEqualTo(hihi);
    }

    @Test
    @DisplayName("label points to a name")
    public void linksByName() {
        //given
        ItemDescription hihi = new ItemDescription();
        hihi.setIdentifier("foo");
        hihi.setName("bar");
        landscapeDescription.mergeItems(List.of(hihi));

        one.getLabels().put("FOO_HOST", hihi.getName());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "FOO_HOST");

        //then
        assertThat(foo).isNotEmpty();
        List<ItemDescription> createdOrModifiedDescriptions = foo.get().getCreatedOrModifiedDescriptions();
        assertThat(createdOrModifiedDescriptions.size()).isEqualTo(2);

        ItemDescription created = createdOrModifiedDescriptions.get(1);
        assertThat(created).isEqualTo(hihi);
    }

    @Test
    @DisplayName("label points to a name but key contains no special word")
    public void linksNotByName() {
        //given
        ItemDescription hihi = new ItemDescription();
        hihi.setIdentifier("foo");
        hihi.setName("bar");
        landscapeDescription.mergeItems(List.of(hihi));

        one.getLabels().put("FOO", hihi.getName());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "FOO");

        //then
        assertThat(foo).isEmpty();
    }

    @Test
    @DisplayName("does not link same service to itself")
    public void linksByAddress() {
        //given
        ItemDescription hihi = new ItemDescription();
        hihi.setIdentifier("something");
        hihi.setAddress("http://foo.bar.com");
        landscapeDescription.mergeItems(List.of(hihi));

        one.getLabels().put("FOO_URL", hihi.getAddress());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "FOO_URL");

        //then
        assertThat(foo).isNotEmpty();
        List<ItemDescription> createdOrModifiedDescriptions = foo.get().getCreatedOrModifiedDescriptions();
        assertThat(createdOrModifiedDescriptions.size()).isEqualTo(2);

        ItemDescription created = createdOrModifiedDescriptions.get(1);
        assertThat(created).isEqualTo(hihi);
    }

    @Test
    @DisplayName("address is compared without case")
    public void linksByAddressCaseInsensitive() {
        //given
        ItemDescription hihi = new ItemDescription();
        hihi.setIdentifier("something");
        hihi.setAddress("http://FOO.bar.com");
        landscapeDescription.mergeItems(List.of(hihi));

        one.getLabels().put("FOO_URL", "http://foo.bar.com");

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "FOO_URL");

        //then
        assertThat(foo).isNotEmpty();
        List<ItemDescription> createdOrModifiedDescriptions = foo.get().getCreatedOrModifiedDescriptions();
        assertThat(createdOrModifiedDescriptions.size()).isEqualTo(2);

        ItemDescription created = createdOrModifiedDescriptions.get(1);
        assertThat(created).isEqualTo(hihi);
    }

    @Test
    @DisplayName("does not link same service to itself")
    public void doesNotLinkSame() {
        //given
        one.getLabels().put("BASE_URL", one.getIdentifier());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "foo");

        //then
        assertThat(foo).isEmpty();
    }

    @Test
    @DisplayName("does nothing with more than one match")
    public void ifUncertainDoesNotLink() {
        //given
        ItemDescription hihi = new ItemDescription();
        hihi.setIdentifier("foo");
        hihi.setName("bar");

        ItemDescription huhu = new ItemDescription();
        huhu.setIdentifier("bar");
        huhu.setName("bar");
        landscapeDescription.mergeItems(List.of(hihi, huhu));

        one.getLabels().put("FOO_HOST", "bar");

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscapeDescription, one, "FOO_HOST");

        //then
        assertThat(foo).isEmpty();
    }
}