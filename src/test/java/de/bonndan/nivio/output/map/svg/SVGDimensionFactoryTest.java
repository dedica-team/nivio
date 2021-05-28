package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SVGDimensionFactoryTest {

    @Test
    void boundingBoxes() {

        //given
        Group g = new Group("a", "landscapeIdentifier");
        Hex one = new Hex(-3, -3);
        Hex two = new Hex(10, 10);
        Set<Hex> hexes = Set.of(one, two); //usually would be much more, but here it is sufficient
        SVGGroupArea svgGroupArea = new SVGGroupArea(g, hexes, List.of());

        //when
        SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(svgGroupArea), List.of());

        //then
        assertThat(dimension).isNotNull();
        SVGDimension.BoundingBox hex = dimension.hex;
        assertThat(hex).isNotNull();
        assertThat(hex.horMin).isEqualTo(one.q);
        assertThat(hex.vertMin).isEqualTo(one.r);
        assertThat(hex.horMax).isEqualTo(two.q);
        assertThat(hex.vertMax).isEqualTo(two.r);

        SVGDimension.BoundingBox cartesian = dimension.cartesian;
        assertThat(cartesian).isNotNull();
        assertThat(cartesian.horMin).isEqualTo((int)one.toPixel().x);
        assertThat(cartesian.vertMin).isEqualTo((int)one.toPixel().y);
        assertThat(cartesian.horMax).isEqualTo((int)two.toPixel().x);
        assertThat(cartesian.vertMax).isEqualTo((int)two.toPixel().y);
    }

    @Test
    void boundingBoxesWithRelations() {

        //given
        Group g = new Group("a", "landscapeIdentifier");
        Hex one = new Hex(-3, -3);
        Hex two = new Hex(10, 10);
        Set<Hex> hexes = Set.of(one, two); //usually would be much more, but here it is sufficient
        SVGGroupArea svgGroupArea = new SVGGroupArea(g, hexes, List.of());

        //when
        Hex three = new Hex(-10, -10);
        SVGRelation svgRelation = new SVGRelation(new HexPath(List.of(three)), "aaccee", new Relation(ItemFactory.getTestItem("foo", "bar"), ItemFactory.getTestItem("foo", "baz")), null);
        SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(svgGroupArea), List.of(svgRelation));

        //then
        assertThat(dimension).isNotNull();
        SVGDimension.BoundingBox hex = dimension.hex;
        assertThat(hex).isNotNull();
        assertThat(hex.horMin).isEqualTo(three.q);
        assertThat(hex.vertMin).isEqualTo(three.r);
        assertThat(hex.horMax).isEqualTo(two.q);
        assertThat(hex.vertMax).isEqualTo(two.r);

        SVGDimension.BoundingBox cartesian = dimension.cartesian;
        assertThat(cartesian).isNotNull();
        assertThat(cartesian.horMin).isEqualTo((int)three.toPixel().x);
        assertThat(cartesian.vertMin).isEqualTo((int)three.toPixel().y);
        assertThat(cartesian.horMax).isEqualTo((int)two.toPixel().x);
        assertThat(cartesian.vertMax).isEqualTo((int)two.toPixel().y);
    }
}