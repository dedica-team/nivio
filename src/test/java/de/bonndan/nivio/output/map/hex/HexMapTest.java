package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HexMapTest {


    @Test
    public void getPath() {
        Item bar = new Item("foo", "bar");
        LayoutedComponent barComponent = new LayoutedComponent(bar);
        barComponent.x = 0;
        barComponent.y = 0;

        Item baz = new Item("moo", "baz");
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
}