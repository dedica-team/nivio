package de.bonndan.nivio.input.dot;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.LabelToFieldResolver;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.search.ItemIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InputFormatHandlerDotTest {

    private String graph;
    private String digraph;
    private String broken;
    private InputFormatHandlerDot handler;
    private FileFetcher fileFetcher;
    private LandscapeDescription description;

    @BeforeEach
    void setup() {
        graph = "graph {\n" +
                "    { rank=same; white}\n" +
                "    { rank=same; cyan; yellow; pink}\n" +
                "    { rank=same; red; green; blue}\n" +
                "    { rank=same; black}\n" +
                "    white [nivio_owner = Marketing, nivio_software=\"Wordpress 2.0\", nivio_group=FooBar, nivio_frameworks=\"php:7.1,angular:9\"]\n" +
                "\n" +
                "    white -- cyan [nivio_format = json, nivio_type=PROVIDER, nivio_description=\"hello world\"]\n" +
                "    cyan -- blue\n" +
                "    white -- yellow -- green\n" +
                "    white -- pink -- red\n" +
                "\n" +
                "    cyan -- green -- black\n" +
                "    yellow -- red -- black\n" +
                "    pink -- blue -- black\n" +
                "}";

        digraph = "digraph G {\n" +
                "main [nivio_owner = Marketing, nivio_software=\"Wordpress 2.0\", nivio_group=FooBar]\n" +
                "main -> parse -> execute;\n" +
                "main -> init;\n" +
                "main -> cleanup;\n" +
                "execute -> make_string;\n" +
                "execute -> printf\n" +
                "init -> make_string;\n" +
                "main -> printf;\n" +
                "execute -> compare;\n" +
                "}\n";

        broken = "}\nä8234,/";

        fileFetcher = mock(FileFetcher.class);
        handler = new InputFormatHandlerDot(fileFetcher);

        when(fileFetcher.get(any(SourceReference.class), any())).thenReturn(graph);
        description = new LandscapeDescription("test");
    }

    @Test
    void setsItemData() {

        //when
        handler.applyData(mock(SourceReference.class), null, description);

        //then
        Set<ItemDescription> itemDescriptions = description.getItemDescriptions().all();
        assertThat(itemDescriptions).hasSize(8);
        ItemDescription white = description.getItemDescriptions().pick("white", null);

        assertThat(white).isNotNull();
        assertThat(white.getLabel(LabelToFieldResolver.NIVIO_LABEL_PREFIX + "owner")).isEqualTo("Marketing");
        assertThat(white.getLabel(LabelToFieldResolver.NIVIO_LABEL_PREFIX + "software")).isEqualTo("Wordpress 2.0");
        assertThat(white.getLabel(LabelToFieldResolver.NIVIO_LABEL_PREFIX + "group")).isEqualTo("FooBar");
        assertThat(white.getLabel(LabelToFieldResolver.NIVIO_LABEL_PREFIX + "frameworks")).isEqualTo("php:7.1,angular:9");
        Set<RelationDescription> relations = white.getRelations();
        assertThat(relations).isNotEmpty().hasSize(3);
    }

    @Test
    void setsRelations() {

        //when
        handler.applyData(mock(SourceReference.class), null, description);

        //then
        ItemDescription white = description.getItemDescriptions().pick("white", null);

        Set<RelationDescription> relations = white.getRelations();
        assertThat(relations).isNotEmpty().hasSize(3);

        Optional<RelationDescription> yellow = getRelationDescription(relations, "yellow");
        assertThat(yellow).isNotEmpty();
        assertThat(yellow.get().getTarget()).isEqualTo("yellow");

        Optional<RelationDescription> cyan = getRelationDescription(relations, "cyan");
        assertThat(cyan).isNotEmpty();
        assertThat(cyan.get().getTarget()).isEqualTo("cyan");

        Optional<RelationDescription> pink = getRelationDescription(relations, "pink");
        assertThat(pink).isNotEmpty();
        assertThat(pink.get().getTarget()).isEqualTo("pink");
    }

    @Test
    void setsRelationDetails() {

        //when
        handler.applyData(mock(SourceReference.class), null, description);

        //then
        ItemDescription white = description.getItemDescriptions().pick("white", null);

        Set<RelationDescription> relations = white.getRelations();

        Optional<RelationDescription> cyan = getRelationDescription(relations, "cyan");
        assertThat(cyan).isNotEmpty();
        assertThat(cyan.get().getTarget()).isEqualTo("cyan");
        assertThat(cyan.get().getFormat()).isEqualTo("json");
        assertThat(cyan.get().getDescription()).isEqualTo("hello world");
        assertThat(cyan.get().getType()).isEqualTo(RelationType.PROVIDER);
    }

    @Test
    void readsDigraph() {

        when(fileFetcher.get(any(SourceReference.class), any())).thenReturn(digraph);

        //when
        handler.applyData(mock(SourceReference.class), null, description);

        //then
        ItemIndex<ItemDescription> itemDescriptions = description.getItemDescriptions();
        itemDescriptions.pick("main", null);
        assertThat(itemDescriptions.all()).hasSize(8);
    }

    @Test
    void brokenInput() {

        when(fileFetcher.get(any(SourceReference.class), any())).thenReturn(broken);

        assertThrows(ProcessingException.class, () -> {handler.applyData(mock(SourceReference.class), null, description);});

    }

    private Optional<RelationDescription> getRelationDescription(Set<RelationDescription> relations, String target) {
        return relations.stream().filter(relationDescription -> relationDescription.getTarget().equals(target)).findFirst();
    }
}