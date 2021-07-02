package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.util.URLHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemIndexTest {

    private ArrayList<Item> items;
    private Landscape landscape;

    @BeforeEach
    public void setup() throws URISyntaxException {

        landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();

        items = new ArrayList<>();

        Item s1 = getTestItemBuilder("g1", "s1").withName("foo").withLandscape(landscape).build();
        items.add(s1);

        Item s2 = getTestItemBuilder("g1", "s2").withName("bar").withLandscape(landscape).build();
        items.add(s2);

        Item s3 = getTestItemBuilder("g2", "hasaddress").withAddress(new URI("https://foo.bar/")).withLandscape(landscape).build();
        items.add(s3);

        landscape.setItems(new HashSet<>(items));
    }

    @Test
    public void pickFails() {

        assertThrows(RuntimeException.class, () -> landscape.getItems().pick("s1", "xxx"));
        assertThrows(RuntimeException.class, () -> landscape.getItems().pick("s3", "g1"));
    }


    @Test
    public void pick() {
        assertNotNull(landscape.getItems().pick("s1", "g1"));
        assertNotNull(landscape.getItems().pick("s2", "g1"));

        ItemDescription s2 = new ItemDescription("s2");

        assertNotNull(landscape.getItems().pick(s2));
    }

    @Test
    public void pickGracefulWithoutGroup() {

        assertNotNull(landscape.getItems().pick("s2", null));
    }

    @Test
    public void pickGracefulFails() {

        Item s2 = getTestItem("g2", "s2", landscape);
        items.add(s2);
        landscape.setItems(new HashSet<>(items));

        assertThrows(RuntimeException.class, () -> landscape.getItems().pick("s2", null));
    }

    @Test
    public void queryUrl() {
        //given
        landscape.getSearchIndex().indexForSearch(landscape, new Assessment(Map.of()));

        //when
        Collection<Item> search = landscape.getItems().query("https://foo.bar/");

        //then
        assertThat(search).isNotEmpty();
        Item next = search.iterator().next();
        assertThat(next.getIdentifier()).isEqualTo("hasaddress");
    }

    @Test
    public void retrieve() {
        //given
        landscape.getSearchIndex().indexForSearch(landscape, new Assessment(Map.of()));

        //when
        Set<FullyQualifiedIdentifier> q = Set.of(items.get(0).getFullyQualifiedIdentifier(), items.get(1).getFullyQualifiedIdentifier());
        Collection<Item> search = landscape.getItems().retrieve(q);

        //then
        assertThat(search).isNotEmpty();
        assertThat(search).hasSize(2);
        assertThat(search).contains(items.get(0));
        assertThat(search).contains(items.get(1));
    }
}
