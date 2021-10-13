package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class ApiModelTest {

    private Landscape landscape;
    private Group group;
    private ItemBuilder itemTemplate;

    @BeforeEach
    void setUp() {
        landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
        group = new Group("g1", landscape.getIdentifier());
        itemTemplate = getTestItemBuilder("g1", "a").withLandscape(landscape).withType("two");
    }

    @Test
    void labelsAreNotGroupedInApi() {

        Item s1 = itemTemplate.build();
        s1.getLabels().put("foo.one", "one");
        s1.getLabels().put("foo.two", "two");

        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());

        //when
        Map<String, String> labels = itemApiModel.getLabels();

        //then
        assertThat(labels).containsKey("foo.one").containsKey("foo.two");
    }

    @Test
    void labelsAreExcludedInJson() {

        Item s1 = itemTemplate.build();
        s1.getLabels().put(InputFormatHandlerKubernetes.LABEL_PREFIX + ".foo", "one");

        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());


        //when
        Map<String, String> labels = itemApiModel.getLabels();

        //then
        assertThat(labels).doesNotContainKey(InputFormatHandlerKubernetes.LABEL_PREFIX + ".foo");
    }
}
