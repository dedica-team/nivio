package de.bonndan.nivio.output.map;

import j2html.tags.DomContent;

import static j2html.TagCreator.div;

class NLabel extends Component {

    private final ItemMapItem item;
    private final String className;
    final int width, size, padding;

    NLabel(ItemMapItem item, String className, int width, int size, int padding) {
        this.item = item;
        this.className = className;
        this.width = width;
        this.size = size;
        this.padding = padding;
    }


    public DomContent render() {
        var style = "stroke: " + item.status;

        /*
        {item.landscapeItem.description && (<div>"{item.landscapeItem.description}"<br/></div>)}
        {item.landscapeItem.owner && (<div>Owner: {item.landscapeItem.owner}</div>)}
        {item.landscapeItem.team && (<div>Team: {item.landscapeItem.team}</div>)}
        {item.landscapeItem.contact && (<div>Contact: {item.landscapeItem.contact}</div>)}
        {item.landscapeItem.software && (<div>Software: {item.landscapeItem.software}</div>)}
        {item.landscapeItem.version && (<div>Version: {item.landscapeItem.version}</div>)}
        {item.landscapeItem.lifecycle && (<div>Lifecycle: {item.landscapeItem.lifecycle}</div>)}
         */

        var rect = SvgTagCreator.rect()
                .attr("x", size + padding)
                .attr("y", -10)
                .attr("rx", 10)
                .attr("ry", 10)
                .attr("fill", "white")
                .attr("width", width)
                .attr("height", size / 2)
                .attr("style", style);
        NText nText = new NText(item, size + padding + (width / 2), 5, "", width);
        var fo = SvgTagCreator.foreignObject(div("").attr("class", "details"))
                .attr("width", width)
                .attr("height", 220)
                .attr("y", padding)
                .attr("x", size + padding);

        return SvgTagCreator.g(rect, nText.render(), fo).attr("class", "label");
    }
}

