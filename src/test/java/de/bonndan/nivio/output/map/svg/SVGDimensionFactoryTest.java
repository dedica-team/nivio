package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.RelationFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexPath;
import de.bonndan.nivio.output.map.hex.MapTile;
import de.bonndan.nivio.output.map.hex.PathTile;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SVGDimensionFactoryTest {

    @Test
    void boundingBoxes() {

        //given
        Group g = new Group("a", "landscapeIdentifier");
        MapTile one = new MapTile(new Hex(-3, -3));
        MapTile two = new MapTile(new Hex(10, 10));
        Set<MapTile> hexes = Set.of(one, two); //usually would be much more, but here it is sufficient
        SVGGroupArea svgGroupArea = new SVGGroupArea(g, hexes, List.of());

        //when
        SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(svgGroupArea), List.of());

        //then
        assertThat(dimension).isNotNull();
        SVGDimension.BoundingBox hex = dimension.hex;
        assertThat(hex).isNotNull();
        assertThat(hex.horMin).isEqualTo(one.getHex().q);
        assertThat(hex.vertMin).isEqualTo(one.getHex().r);
        assertThat(hex.horMax).isEqualTo(two.getHex().q);
        assertThat(hex.vertMax).isEqualTo(two.getHex().r);

        SVGDimension.BoundingBox cartesian = dimension.cartesian;
        assertThat(cartesian).isNotNull();
        assertThat(cartesian.horMin).isEqualTo((int) one.getHex().toPixel().x);
        assertThat(cartesian.vertMin).isEqualTo((int) one.getHex().toPixel().y);
        assertThat(cartesian.horMax).isEqualTo((int) two.getHex().toPixel().x);
        assertThat(cartesian.vertMax).isEqualTo((int) two.getHex().toPixel().y);
    }

    @Test
    void boundingBoxesWithRelations() {

        //given
        Group g = new Group("a", "landscapeIdentifier");
        MapTile one = new MapTile(new Hex(-3, -3));
        MapTile two = new MapTile(new Hex(10, 10));
        Set<MapTile> hexes = Set.of(one, two); //usually would be much more, but here it is sufficient
        SVGGroupArea svgGroupArea = new SVGGroupArea(g, hexes, List.of());

        //when
        PathTile three = new PathTile(new MapTile(new Hex(-10, -10)));
        PathTile four = new PathTile(new MapTile(new Hex(-11, -10)));
        SVGRelation svgRelation = new SVGRelation(new HexPath(List.of(three, four)), "aaccee", RelationFactory.createForTesting(ItemFactory.getTestItem("foo", "bar"), ItemFactory.getTestItem("foo", "baz")), null);
        SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(svgGroupArea), List.of(svgRelation));

        //then
        assertThat(dimension).isNotNull();
        SVGDimension.BoundingBox hex = dimension.hex;
        assertThat(hex).isNotNull();
        assertThat(hex.horMin).isEqualTo(four.getMapTile().getHex().q);
        assertThat(hex.vertMin).isEqualTo(four.getMapTile().getHex().r);
        assertThat(hex.horMax).isEqualTo(two.getHex().q);
        assertThat(hex.vertMax).isEqualTo(two.getHex().r);

        SVGDimension.BoundingBox cartesian = dimension.cartesian;
        assertThat(cartesian).isNotNull();
        assertThat(cartesian.horMin).isEqualTo((int) four.getMapTile().getHex().toPixel().x);
        assertThat(cartesian.vertMin).isEqualTo((int) four.getMapTile().getHex().toPixel().y);
        assertThat(cartesian.horMax).isEqualTo((int) two.getHex().toPixel().x);
        assertThat(cartesian.vertMax).isEqualTo((int) two.getHex().toPixel().y);
    }
}