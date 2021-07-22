package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class HintTest {

    private ItemDescription source;
    private ItemDescription target;

    @BeforeEach
    void setup() {
        source = new ItemDescription();
        source.setIdentifier("foo");
        target = new ItemDescription();
        source.setIdentifier("bar");
    }

    @Test
    void useSetsType() {
        Hint hint = new Hint(ItemType.SERVICE, RelationType.DATAFLOW, null);

        //when
        hint.use(source, target, Optional.empty());

        //then
        assertThat(target.getType()).isEqualTo(ItemType.SERVICE);
    }

    @Test
    void useSetsSoftware() {
        Hint hint = new Hint(ItemType.SERVICE, RelationType.DATAFLOW, "FooBar 2.0");

        //when
        hint.use(source, target, Optional.empty());

        //then
        assertThat(target.getLabel(Label.software)).isEqualTo("FooBar 2.0");
    }

    @Test
    void returnsAll() {
        Hint hint = new Hint(ItemType.SERVICE, RelationType.DATAFLOW, "FooBar 2.0");

        //when
        hint.use(source, target, java.util.Optional.empty());

        //then
        assertThat(hint.getCreatedOrModifiedDescriptions()).isNotNull();
        assertThat(hint.getCreatedOrModifiedDescriptions().size()).isEqualTo(2);
        assertThat(hint.getCreatedOrModifiedDescriptions().get(0)).isEqualTo(source);
        assertThat(hint.getCreatedOrModifiedDescriptions().get(1)).isEqualTo(target);
    }

    @Test
    void createsRelation() {
        Hint hint = new Hint(ItemType.SERVICE, RelationType.DATAFLOW, null);

        //when
        hint.use(source, target, java.util.Optional.empty());

        //then
        Set<RelationDescription> relations = hint.getCreatedOrModifiedDescriptions().get(0).getRelations();
        RelationDescription relation = relations.iterator().next();
        assertThat(relation).isNotNull();
        assertThat(relation.getSource()).isEqualTo(source.getIdentifier());
        assertThat(relation.getTarget()).isEqualTo(target.getIdentifier());
        assertThat(relation.getType()).isEqualTo(RelationType.DATAFLOW);
    }

    @Test
    void createsInverseRelation() {
        Hint hint = new Hint(ItemType.DATABASE, RelationType.PROVIDER, null);

        //when
        hint.use(source, target, java.util.Optional.empty());

        //then
        Set<RelationDescription> relations = hint.getCreatedOrModifiedDescriptions().get(0).getRelations();
        RelationDescription relation = relations.iterator().next();
        assertThat(relation).isNotNull();
        assertThat(relation.getSource()).isEqualTo(target.getIdentifier());
        assertThat(relation.getTarget()).isEqualTo(source.getIdentifier());
        assertThat(relation.getType()).isEqualTo(RelationType.PROVIDER);
    }

    @Test
    void doesNotOverwriteExistingRelation() {
        Hint hint = new Hint(ItemType.DATABASE, RelationType.PROVIDER, null);

        RelationDescription relation = new RelationDescription(source.getIdentifier(), target.getIdentifier());
        relation.setType(RelationType.DATAFLOW);
        source.addOrReplaceRelation(relation);

        //when
        hint.use(source, target, Optional.of(relation));

        //then
        Set<RelationDescription> relations = hint.getCreatedOrModifiedDescriptions().get(0).getRelations();
        RelationDescription r1 = relations.iterator().next();
        assertThat(r1).isNotNull();
        assertThat(r1).isEqualTo(relation);
        assertThat(r1.getType()).isEqualTo(RelationType.DATAFLOW);
    }

    @Test
    void setsTypeOnExistingRelation() {
        Hint hint = new Hint(ItemType.DATABASE, RelationType.PROVIDER, null);

        RelationDescription relation = new RelationDescription(source.getIdentifier(), target.getIdentifier());
        source.addOrReplaceRelation(relation);

        //when
        hint.use(source, target, Optional.of(relation));

        //then
        Set<RelationDescription> relations = hint.getCreatedOrModifiedDescriptions().get(0).getRelations();
        RelationDescription r1 = relations.iterator().next();
        assertThat(r1).isNotNull();
        assertThat(r1).isEqualTo(relation);
        assertThat(r1.getType()).isEqualTo(RelationType.PROVIDER);
    }
}