package de.bonndan.nivio;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.ContextDescription;
import de.bonndan.nivio.input.dto.UnitDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.NullSearchIndex;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.NoSuchElementException;

/**
 * A simple pre-built graph for testing purposes. Ensure that all created components are attached to the graph.
 *
 * The default instance has no usable search index.
 */
public class GraphTestSupport {

    public final Landscape landscape;
    @NonNull
    public final Index<GraphComponent> index;
    public final Group groupA;

    /**
     * empty by default
     */
    public final Group groupB;
    public final Group groupC;

    public final Item itemAA;
    public final Item itemAB;
    public final Item itemAC;

    public final Unit unit;
    public final Context context;

    public GraphTestSupport() {
        this(new Index<>(new NullSearchIndex()));
    }

    public GraphTestSupport(@Nullable final Index<GraphComponent> index) {
        this.index = index;
        landscape = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();
        landscape.setLog(new ProcessLog(LoggerFactory.getLogger(GraphTestSupport.class), "test"));

        unit = UnitFactory.INSTANCE.createFromDescription(Landscape.DEFAULT_COMPONENT, landscape, new UnitDescription());
        landscape.getIndexWriteAccess().addOrReplaceChild(unit);
        context = ContextFactory.INSTANCE.createFromDescription(Landscape.DEFAULT_COMPONENT, unit, new ContextDescription());
        landscape.getIndexWriteAccess().addOrReplaceChild(context);

        groupA = getTestGroup("a");
        groupB = getTestGroup("b");
        groupC = getTestGroup("c");

        itemAA = getTestItemBuilder("a", "a").withParent(groupA).build();
        itemAB = getTestItemBuilder("a", "b").withParent(groupA).build();
        itemAC = getTestItemBuilder("a", "c").withParent(groupA).build();
        landscape.getIndexWriteAccess().addOrReplaceChild(itemAA);
        landscape.getIndexWriteAccess().addOrReplaceChild(itemAB);
        landscape.getIndexWriteAccess().addOrReplaceChild(itemAC);
    }

    public Item getTestItem(String groupId, String id) {
        Group group = landscape.getGroup(groupId).orElseThrow();
        Item item = ItemBuilder.anItem().withIdentifier(id).withParent(group).build();
        landscape.getIndexWriteAccess().addOrReplaceChild(item);
        assert item.isAttached();
        return item;
    }

    public ItemBuilder getTestItemBuilder(String groupId, String id) {
        Group group = landscape.getGroup(groupId).orElseThrow(() -> new NoSuchElementException("Invalid test item group " + groupId));
        return ItemBuilder.anItem().withIdentifier(id).withParent(group);
    }

    public void indexForSearch(Assessment assessment) {
        landscape.getIndexReadAccess().indexForSearch(assessment);
    }

    public Group getTestGroup(String identifier) {
        Group group = GroupBuilder.aGroup().withIdentifier(identifier).withName(identifier).withParent(context).build();
        landscape.getIndexWriteAccess().addOrReplaceChild(group);
        return group;
    }
}
