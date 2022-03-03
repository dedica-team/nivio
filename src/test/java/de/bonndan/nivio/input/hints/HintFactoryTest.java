package de.bonndan.nivio.input.hints;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HintFactoryTest {

    private HintFactory hintFactory;
    private LandscapeDescription landscape;
    private ItemDescription itemAA;

    @BeforeEach
    void setUp() {
        landscape = new LandscapeDescription("test");

        itemAA = new ItemDescription("a");
        itemAA.setGroup("a");
        landscape.getWriteAccess().addOrReplaceChild(itemAA);

        hintFactory = new HintFactory();
    }

    @Test
    void createWithMysqlURI() {

        //given
        itemAA.setLabel("foo", "mysql://somehost/abc");

        var target = new ItemDescription("someId");
        target.setAddress("mysql://somehost/abc");
        landscape.getWriteAccess().addOrReplaceChild(target);
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "foo");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getRelationType()).isEqualTo(RelationType.PROVIDER);
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier().toString());
        assertThat(foo.get().getTargetType()).isEqualTo(ItemType.DATABASE);
    }

    @Test
    void createWithHttpURI() {

        //given
        itemAA.setLabel("foo", "http://foo.bar.baz");

        var target = new ItemDescription("foo");
        target.setGroup("a");
        target.setAddress("http://foo.bar.baz");
        landscape.getWriteAccess().addOrReplaceChild(target);
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "foo");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getRelationType()).isEqualTo(RelationType.DATAFLOW);
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier().toString());
    }

    @Test
    @DisplayName("links with identifier")
    void linksByIdentifier() {

        //given
        var target = new ItemDescription("foo");
        target.setGroup("a");
        target.setAddress("http://foo.bar.baz");
        landscape.getWriteAccess().addOrReplaceChild(target);

        itemAA.setLabel("BASE_URL", target.getIdentifier());
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "BASE_URL");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier().toString());
    }

    @Test
    @DisplayName("label points to a name")
    void linksByName() {
        //given
        var target = new ItemDescription("foo");
        target.setGroup("a");
        target.setName("aName");
        landscape.getWriteAccess().addOrReplaceChild(target);

        itemAA.setLabel("BASE_URL", target.getName());
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "BASE_URL");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier().toString());
    }

    @Test
    @DisplayName("label points to a name but key contains no special word")
    void linksNotByName() {
        //given
        var target = new ItemDescription("foo");
        target.setGroup("a");
        target.setName("aName");
        landscape.getWriteAccess().addOrReplaceChild(target);

        itemAA.setLabel("FOO", target.getName());
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "foo");

        //then
        assertThat(foo).isEmpty();
    }

    @Test
    @DisplayName("does not link same item to itself")
    void doesNotLinkSame() {
        //given
        itemAA.setLabel("BASE_URL", itemAA.getName());
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "foo");

        //then
        assertThat(foo).isEmpty();
    }

    @Test
    @DisplayName("more than one match can be matched by group")
    void twoTargetsMatchedByGroups() {
        //given
        String aName = "aName";
        var target1 = new ItemDescription("foo1");
        target1.setGroup(itemAA.getGroup());
        target1.setName(aName);
        landscape.getWriteAccess().addOrReplaceChild(target1);

        var target2 = new ItemDescription("foo1");
        target2.setGroup("b");
        target2.setName(aName);
        landscape.getWriteAccess().addOrReplaceChild(target2);

        itemAA.setLabel("BASE_URL", aName);
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "BASE_URL");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getTarget()).isEqualTo(target1.getFullyQualifiedIdentifier().toString());
    }

    @Test
    @DisplayName("more than one match is empty")
    void addsTwoHints() {
        //given
        String aName = "aName";
        var target1 = new ItemDescription("foo1");
        target1.setName(aName);
        landscape.getWriteAccess().addOrReplaceChild(target1);

        var target2 = new ItemDescription("foo2");
        target2.setName(aName);
        landscape.getWriteAccess().addOrReplaceChild(target2);

        itemAA.setLabel("BASE_URL", aName);
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(landscape.getReadAccess(), itemAA, "BASE_URL");

        //then
        assertThat(foo).isEmpty();
    }
}