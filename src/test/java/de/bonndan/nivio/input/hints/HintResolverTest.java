package de.bonndan.nivio.input.hints;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.LabelToFieldResolver;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HintResolverTest {

    private HintResolver hintResolver;
    private ProcessLog log;
    private LandscapeDescription landscape;

    @BeforeEach
    public void setup() {

        log = new ProcessLog(mock(Logger.class), "test");
        hintResolver = new HintResolver(new HintFactory(), log);

        landscape = new LandscapeDescription("test");
    }

    @Test
    @DisplayName("label blacklist is used")
    void blacklistPreventsRelations() {
        //given
        landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        var bar = new ItemDescription("bar");
        bar.setGroup("a");
        bar.setLabel("BAZ_COMPOSITION_URL", "http://baz-composition-service:80");
        landscape.getWriteAccess().addOrReplaceChild(bar);

        var baz = new ItemDescription("baz");
        baz.setGroup("a");
        baz.setAddress("http://baz-composition-service:80");
        landscape.getWriteAccess().addOrReplaceChild(baz);

        //when
        hintResolver.resolve(landscape);

        //then
        Map<URI, List<Hint>> hints = log.getHints();
        assertThat(hints).isNotNull()
                .doesNotContainKey(bar.getFullyQualifiedIdentifier())
                .doesNotContainKey(baz.getFullyQualifiedIdentifier());

    }

    @Test
    @DisplayName("label blacklist is used case insensitive")
    void blacklistPreventsRelationsCaseInsensitive() {

        //given
        landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        var bar = new ItemDescription("bar");
        bar.setGroup("a");
        bar.setLabel("BAZ_COMPOSITION_URL", "http://baz-composition-service:80");
        landscape.getWriteAccess().addOrReplaceChild(bar);

        var baz = new ItemDescription("baz");
        baz.setGroup("a");
        baz.setAddress("http://baz-composition-service:80");
        landscape.getWriteAccess().addOrReplaceChild(baz);

        //when
        hintResolver.resolve(landscape);

        //then
        Map<URI, List<Hint>> hints = log.getHints();
        assertThat(hints).isNotNull()
                .doesNotContainKey(bar.getFullyQualifiedIdentifier())
                .doesNotContainKey(baz.getFullyQualifiedIdentifier());
    }

    @Test
    void readsLinks() {

        //given
        var db = new ItemDescription("x.y.z");
        db.setGroup("a");
        db.setLabel(LabelToFieldResolver.LINK_LABEL_PREFIX + "foo", "http://foo.bar.baz");
        landscape.getWriteAccess().addOrReplaceChild(db);

        //when
        hintResolver.resolve(landscape);

        //then
        Map<URI, List<Hint>> hints = log.getHints();
        assertThat(hints).isNotNull()
                .containsKey(db.getFullyQualifiedIdentifier());
    }

    @Test
    void addsNewItems() {

        landscape = new LandscapeDescription("identifier", "name", null);
        ItemDescription a = new ItemDescription();
        a.setIdentifier("a");
        a.setGroup("a");
        a.getProvidedBy().add("providerA");
        landscape.getWriteAccess().addOrReplaceChild(a);

        ItemDescription b = new ItemDescription();
        b.setIdentifier("b");
        b.setGroup("b");
        b.setRelations(Arrays.asList("adistanttarget"));

        landscape.getWriteAccess().addOrReplaceChild(b);

        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        hintResolver.resolve(landscape);

        //then
        assertThat(log.getHints()).hasSize(2)
                .containsKey(a.getFullyQualifiedIdentifier())
                .containsKey(b.getFullyQualifiedIdentifier());
        List<Hint> aHint = log.getHints().get(a.getFullyQualifiedIdentifier());
        assertThat(aHint).isNotEmpty();
    }

    @Test
    void addsNewItemsOnlyOnce() {

        landscape = new LandscapeDescription("identifier", "name", null);
        ItemDescription a = new ItemDescription();
        a.setIdentifier("a");
        a.setGroup("a");
        a.getProvidedBy().add("providerA");
        landscape.getWriteAccess().addOrReplaceChild(a);

        ItemDescription b = new ItemDescription();
        b.setIdentifier("b");
        b.setGroup("b");
        landscape.getWriteAccess().addOrReplaceChild(b);

        ItemDescription c = new ItemDescription();
        c.setIdentifier("c");
        c.setGroup("c");
        c.setRelations(Arrays.asList("adistanttarget"));
        landscape.getWriteAccess().addOrReplaceChild(c);

        landscape.getReadAccess().indexForSearch(Assessment.empty());

        //when
        hintResolver.resolve(landscape);

        //3 given plus 2 resolved (like above)
        Map<URI, List<Hint>> hints = log.getHints();
        assertThat(hints).hasSize(2);

        List<Hint> aHint = hints.get(a.getFullyQualifiedIdentifier());
        assertThat(aHint).isNotEmpty().hasSize(1);
        assertThat(aHint.get(0).getTarget()).isEqualToIgnoringCase("providera");

        List<Hint> cHint = hints.get(c.getFullyQualifiedIdentifier());
        assertThat(cHint).isNotEmpty().hasSize(1);
        assertThat(cHint.get(0).getTarget()).isEqualToIgnoringCase("adistanttarget");
    }
}