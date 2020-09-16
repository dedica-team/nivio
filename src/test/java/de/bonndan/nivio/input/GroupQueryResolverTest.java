package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GroupQueryResolverTest {

    private GroupQueryResolver groupResolver;

    @Mock
    private ProcessLog processLog;

    @BeforeEach
    public void setup() {
        groupResolver = new GroupQueryResolver(processLog);
    }

    @Test
    void process_doesNotContainDuplicatedItemsInGroups() {
        LandscapeImpl landscape = LandscapeFactory.create("test");
        landscape.setProcessLog(processLog);
        Item item = new Item("groupIdentifier", "itemIdentifier");
        landscape.setItems(Set.of(item));
        landscape.addGroup(new Group("groupIdentifier"));

        LandscapeDescription input = new LandscapeDescription();
        GroupDescription groupDescription = new GroupDescription();
        groupDescription.setIdentifier("groupIdentifier");
        groupDescription.setContains(List.of("itemidentifier"));
        input.getGroups().put("group", groupDescription);

        groupResolver.process(input, landscape);

        assertThat(landscape.getItems().stream().count()).isEqualTo(1L);
        assertThat(landscape.getGroup("groupIdentifier").get().getItems()).containsExactly(item);
    }

    @Test
    void process_findsItemsIfQueryConditionIsNotLowercase() {
        LandscapeImpl landscape = LandscapeFactory.create("test");
        landscape.setProcessLog(processLog);
        Item item = new Item("groupIdentifier", "itemIdentifier");
        landscape.setItems(Set.of(item));
        landscape.addGroup(new Group("groupIdentifier"));

        LandscapeDescription input = new LandscapeDescription();
        GroupDescription groupDescription = new GroupDescription();
        groupDescription.setIdentifier("groupIdentifier");
        groupDescription.setContains(List.of("itemIdentifier"));
        input.getGroups().put("group", groupDescription);

        groupResolver.process(input, landscape);

        assertThat(landscape.getItems().stream().count()).isEqualTo(1L);
        assertThat(landscape.getGroup("groupIdentifier")).isPresent();
        assertThat(landscape.getGroup("groupIdentifier").get().getItems()).containsExactly(item);
    }

    @Test
    void process_containsCommonGroupIfGroupWasEmpty() {
        /*
         * I have removed the magic from landscape.getGroup() and made that LandscapeFactory creates the COMMON group
         * as default. I've also added a test, so this test is obsolete.
         */
        LandscapeImpl landscape = LandscapeFactory.create("test");
        landscape.setProcessLog(processLog);
        Item item = new Item(null, "itemIdentifier");

        landscape.setItems(Set.of(item));

        LandscapeDescription input = new LandscapeDescription();
        GroupDescription groupDescription = new GroupDescription();

        input.getGroups().put("group", groupDescription);

        groupResolver.process(input, landscape);

        assertThat(landscape.getItems().stream().count()).isEqualTo(1L);
        assertThat(landscape.getGroups().values().size()).isEqualTo(1);
        assertThat(landscape.getGroups().values().stream().findFirst().get().getIdentifier()).isEqualTo("common");
    }

    @Test
    void process_withLandscapeFromLandscapeFactory_containsCommonGroupIfNoGroupWasSet() {
        LandscapeImpl landscape = LandscapeFactory.create("landscapeIdentifier");
        Item item = new Item(null, "itemIdentifier");

        landscape.setItems(Set.of(item));

        LandscapeDescription input = new LandscapeDescription();
        GroupDescription groupDescription = new GroupDescription();

        input.getGroups().put("group", groupDescription);

        groupResolver.process(input, landscape);

        assertThat(landscape.getItems().stream().count()).isEqualTo(1L);
        assertThat(landscape.getGroups().values().size()).isEqualTo(1);
        assertThat(landscape.getGroups().values().stream().findFirst().get().getIdentifier()).isEqualTo("common");

    }

}