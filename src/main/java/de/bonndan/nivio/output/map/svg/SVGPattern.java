package de.bonndan.nivio.output.map.svg;

import j2html.tags.DomContent;

import java.util.Base64;
import static j2html.TagCreator.rawHtml;

class SVGPattern extends Component {

    private final String id, link;
    private final int size;

    SVGPattern(String link, int size) {
        this.id = idForLink(link);
        this.link = link;
        this.size = size;
    }

    static String idForLink(String link) {
        return Base64.getEncoder().encodeToString(link.getBytes());
    }

    public DomContent render() {

        return rawHtml(
                    "<pattern id=\"" + id + "\" patternUnits=\"objectBoundingBox\" x=\"0\" y=\"0\" width=\"" + size + "\" height=\"" + size + "\" >" +
                    "<rect height=\"100\" width=\"100\" fill=\"white\" />" +
                    "<image xlink:href=\"" + link + "\" width=\"" + size * 2 + "\" height=\"" + size * 2 + "\"  />" +
                    "</pattern>"

        );
    }
}

