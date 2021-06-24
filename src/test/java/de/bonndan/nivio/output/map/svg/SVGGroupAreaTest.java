package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;

class SVGGroupAreaTest {

    @Test
    public void hasFQI() {
        Hex e1 = new Hex(1, 1, -2);
        Hex e2 = new Hex(3, 3, -6);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item landscapeItem2 = getTestItem("group", "bar");

        Group group = new Group("group", "foo");
        group.addItem(landscapeItem);
        group.addItem(landscapeItem2);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(e1, landscapeItem);
        hexesToItems.put(e2, landscapeItem2);

        Set<Hex> area = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), group);
        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(group, area, new StatusValue("foo", Status.GREEN), false);

        assertThat(svgGroupArea.render().render()).contains(group.getFullyQualifiedIdentifier().jsonValue());
    }
}