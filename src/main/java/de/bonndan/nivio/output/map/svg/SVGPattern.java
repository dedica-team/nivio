package de.bonndan.nivio.output.map.svg;

import j2html.tags.DomContent;

import java.util.Base64;

import static j2html.TagCreator.rawHtml;

class SVGPattern extends Component {

    private final String id, link;

    SVGPattern(String link) {
        this.id = idForLink(link);
        this.link = link;
    }

    static String idForLink(String link) {
        return Base64.getEncoder().encodeToString(link.getBytes());
    }

    public DomContent render() {

        return rawHtml(
                "<pattern id=\"" + id + "\" patternUnits=\"objectBoundingBox\" x=\"0\" y=\"0\" width=\"100%\" height=\"100%\" >" +
                        "<rect height=\"100\" width=\"100\" fill=\"white\" />" +
                        "<image xlink:href=\"" + link + "\" width=\"200\" height=\"200\" />" +
                        "</pattern>"
        );
    }
}

