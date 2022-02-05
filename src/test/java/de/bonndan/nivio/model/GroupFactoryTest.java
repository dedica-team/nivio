package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupFactoryTest {

    private Landscape landscape;
    private Context context;
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        landscape = graph.landscape;
        context = graph.context;
    }

    @Test
    @DisplayName("Merged groups uses all updates where not null")
    void testMerge() {

        Group one = GroupBuilder.aGroup()
                .withIdentifier("a")
                .withDescription("a")
                .withName("test")
                .withOwner("Joe")
                .withContact("mail")
                .withColor("#123123")
                .withParent(context)
                .build();
        landscape.getIndexWriteAccess().addOrReplaceChild(one);

        Group added = GroupBuilder.aGroup()
                .withIdentifier("a")
                .withName("test")
                .withOwner("Matt")
                .withContact("a")
                .withColor(null)
                .withParent(context)
                .build();

        Group merged = GroupFactory.INSTANCE.merge(one, added);

        assertEquals("test", merged.getName());
        assertEquals("Matt", merged.getOwner());
        assertEquals("a", merged.getDescription());
        assertEquals("a", merged.getContact());
        assertEquals(added.getColor(), merged.getColor());
    }

    @Test
    void usesExistingValues() {

        //given
        Group in = GroupBuilder.aGroup()
                .withIdentifier("a")
                .withName("test")
                .withOwner("Matt")
                .withContact("mail")
                .withColor("#123123")
                .withParent(context)
                .build();
        landscape.getIndexWriteAccess().addOrReplaceChild(in);

        //when
        Group merge = GroupFactory.INSTANCE.merge(in, GroupBuilder.aGroup().withIdentifier("a").withParent(context).build());

        //then
        assertThat(merge).isNotNull();
        assertThat(merge.getIdentifier()).isEqualTo(in.getIdentifier());
        assertThat(merge.getName()).isEqualTo(in.getName());
        assertThat(merge.getDescription()).isEqualTo(in.getDescription());
        assertThat(merge.getContact()).isEqualTo(in.getContact());
        assertThat(merge.getColor()).isEqualTo(in.getColor());
        assertThat(merge.getIcon()).isEqualTo(in.getIcon());
        assertThat(merge.getLinks()).isEqualTo(in.getLinks());
        assertThat(merge.getLabels()).isEqualTo(in.getLabels());
    }

    @Test
    void mergeSetsDefaultColor() {
        Group one = GroupBuilder.aGroup().withIdentifier("a").withName("test").withOwner("Matt").withContact("mail")
                .withParent(context)
                .build();
        landscape.getIndexWriteAccess().addOrReplaceChild(one);

        Group two = GroupBuilder.aGroup().withIdentifier("a").withName("test").withOwner("Matt").withContact("mail")
                .withParent(context)
                .build();

        //when
        Group merge = GroupFactory.INSTANCE.merge(one, two);

        //when
        assertThat(merge).isNotNull();
        assertThat(merge.getColor()).isNotEmpty();
    }
}
