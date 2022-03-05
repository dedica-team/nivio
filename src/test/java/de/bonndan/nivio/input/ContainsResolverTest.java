package de.bonndan.nivio.input;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ContainsResolverTest {

    private ContainsResolver groupResolver;

    private LandscapeDescription input;
    private GroupDescription groupDescription;

    @BeforeEach
    public void setup() {
        groupResolver = new ContainsResolver();

        input = new LandscapeDescription("landscapeIdentifier", "testLandscape", null);
        input.setProcessLog(new ProcessLog(mock(Logger.class), "landscapeIdentifier"));

        groupDescription = new GroupDescription();
        groupDescription.setIdentifier("groupIdentifier");

        input.getWriteAccess().addOrReplaceChild(groupDescription);

    }

    @Test
    void process_doesNotContainDuplicatedItemsInGroups() {

        ItemDescription item = new ItemDescription("itemIdentifier");
        item.setGroup("groupIdentifier");
        input.mergeItems(Set.of(item));

        groupDescription.setContains(List.of("itemIdentifier"));

        input.getReadAccess().indexForSearch(Assessment.empty());

        //when
        groupResolver.resolve(input);

        Set<ItemDescription> matched = input.getItemDescriptions().stream()
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

        input.getReadAccess().indexForSearch(Assessment.empty());

        //when
        groupResolver.resolve(input);

        //then
        Set<ItemDescription> matched = input.getItemDescriptions().stream()
                .filter(itemDescription -> itemDescription.getGroup().equals(groupDescription.getIdentifier()))
                .collect(Collectors.toSet());
        assertThat(matched).containsExactly(item);
    }

}