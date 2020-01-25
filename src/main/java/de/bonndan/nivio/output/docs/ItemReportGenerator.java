package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.IconService;

import static j2html.TagCreator.body;
import static j2html.TagCreator.html;

public class ItemReportGenerator extends ReportGenerator {

    public ItemReportGenerator(IconService iconService) {
        super(iconService);
    }

    public String toDocument(Item item) {
        return  html(body(writeItem(item))).renderFormatted();
    }
}
