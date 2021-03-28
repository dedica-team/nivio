package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GroupQueryResolverTest {

    private GroupQueryResolver groupResolver;

    @Mock
    private ProcessLog processLog;
    private LandscapeDescription input;
    private GroupDescription groupDescription;

    @BeforeEach
    public void setup() {
        groupResolver = new GroupQueryResolver(processLog);

        input = new LandscapeDescription("landscapeIdentifier", "testLandscape", null);
        groupDescription = new GroupDescription();
        groupDescription.setIdentifier("groupIdentifier");

        input.getGroups().put("group", groupDescription);

    }

    @Test
    void process_doesNotContainDuplicatedItemsInGroups() {

        ItemDescription item = new ItemDescription("itemIdentifier");
        item.setGroup("groupIdentifier");
        input.mergeItems(Set.of(item));

        groupDescription.setContains(List.of("itemIdentifier"));

        groupResolver.resolve(input);

        Set<ItemDescription> matched = input.getItemDescriptions().all().stream()
                .filter(itemDescription -> itemDescription.getGroup().equals(groupDescription.getIdentifier()))
                .collect(Collectors.toSet());
        assertThat(matched).containsExactly(item);
    }

    @Test
    void process_findsItemsIfQueryConditionIsNotLowercase() {

        ItemDescription item = new ItemDescription("itemIdentifier");
        item.setGroup("groupIdentifier");
        input.mergeItems(Set.of(item));

        groupDescription.setContains(List.of("itemidentifier"));

        //when
        groupResolver.resolve(input);

        //then
        Set<ItemDescription> matched = input.getItemDescriptions().all().stream()
                .filter(itemDescription -> itemDescription.getGroup().equals(groupDescription.getIdentifier()))
                .collect(Collectors.toSet());
        assertThat(matched).containsExactly(item);
    }

}