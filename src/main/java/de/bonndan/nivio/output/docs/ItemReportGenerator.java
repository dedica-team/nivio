package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.LocalServer;

import static j2html.TagCreator.*;

public class ItemReportGenerator extends ReportGenerator {

    public ItemReportGenerator(IconService iconService, LocalServer localServer) {
        super(iconService, localServer);
    }

    public String toDocument(Item item) {
        return "<!DOCTYPE html>" + html(
                head(),
                body(writeItem(item))
        )
                .attr("xmlns", "http://www.w3.org/1999/xhtml")
                .renderFormatted();
    }
}
