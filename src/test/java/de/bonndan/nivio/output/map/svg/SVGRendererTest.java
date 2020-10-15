package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SVGRendererTest {

    @Test
    void testRendering() throws MalformedURLException {

        //given
        LocalServer localServer = mock(LocalServer.class);
        when(localServer.getIconUrl(any(Item.class))).thenReturn(new URL("https://foo.bar/icon.png"));
        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        SVGRenderer svgRenderer = new SVGRenderer(mapStyleSheetFactory);

        LayoutedComponent lc = getLayoutedLandscape();

        //when
        String render = svgRenderer.render(lc);

        //check svg xml is returned
        assertTrue(render.contains("svg version=\"1.1\""));

        LayoutedComponent itemComponent = lc.getChildren().get(0).getChildren().get(0);
        assertNotNull(itemComponent);


        //check items are shifted
        assertEquals(250, itemComponent.getX()); //margin + group offset + own offset
        assertEquals(266, itemComponent.getY()); //margin + group offset + own offset
    }

    private LayoutedComponent getLayoutedLandscape() {
        Landscape foo = LandscapeFactory.create("foo");

        LayoutedComponent lc = new LayoutedComponent(foo);
        lc.setChildren(new ArrayList<>());

        Group group = new Group("bar");
        foo.addGroup(group);

        LayoutedComponent glc = new LayoutedComponent(group);
        glc.setChildren(new ArrayList<>());
        glc.setWidth(100);
        glc.setHeight(100);
        glc.setX(100);
        glc.setY(100);

        lc.getChildren().add(glc);

        Item baz = new Item(null,"baz");

        LayoutedComponent ilc = new LayoutedComponent(baz);
        glc.setWidth(50);
        glc.setHeight(50);
        glc.setX(50);
        glc.setY(66);

        glc.getChildren().add(ilc);

        return lc;
    }
}