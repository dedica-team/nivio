package de.bonndan.nivio.output.map;

import j2html.tags.DomContent;

import static j2html.TagCreator.rawHtml;

class NPattern extends Component {

    private final String id, link;
    private final int size, padding;

    NPattern(String id, String link, int size, int padding) {
        this.id = id;
        this.link = link;
        this.size = size;
        this.padding = padding;
    }


    public DomContent render() {

        return rawHtml(
                "<defs>" +
                        "<pattern id=\"" + id + "\" patternUnits=\"objectBoundingBox\" x =\"0\" y=\"0\" width=\"" + size + "\" height=\"" + size + "\" >" +
                        "<rect height=\"100\" width=\"100\" fill=\"white\" />" +
                        "<image xlink:href=\"" + link + "\" x=\"" + padding + "\" y=\"" + padding + "\" width=\"" + size * 2 + "\" height=\"" + size * 2 + "\"  />" +
                        "</pattern>" +
                        "</defs >"
        );
    }
}

