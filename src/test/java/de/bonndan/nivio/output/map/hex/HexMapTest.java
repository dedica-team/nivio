package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;

class HexMapTest {


    @Test
    public void getPath() {
        Item bar = getTestItem("foo", "bar");
        LayoutedComponent barComponent = new LayoutedComponent(bar);
        barComponent.x = 0;
        barComponent.y = 0;

        Item baz = getTestItem("moo", "baz");
        LayoutedComponent bazComponent = new LayoutedComponent(baz);
        barComponent.x = 500;
        barComponent.y = 500;

        HexMap hexMap = new HexMap(false);
        hexMap.add(barComponent);
        hexMap.add(bazComponent);

        //when
        Optional<HexPath> path = hexMap.getPath(bar, baz);

        //then
        assertThat(path).isNotEmpty();
    }

    @Test
    void addCreatesHexWithItem() {
        Item bar = getTestItem("foo", "bar");
        LayoutedComponent barComponent = new LayoutedComponent(bar);
        barComponent.x = 0;
        barComponent.y = 0;

        HexMap hexMap = new HexMap(false);

        //when
        Hex added = hexMap.add(barComponent);

        //then
        assertThat(added).isNotNull();
        assertThat(added.item).isEqualTo(bar.getFullyQualifiedIdentifier().toString());
    }
}