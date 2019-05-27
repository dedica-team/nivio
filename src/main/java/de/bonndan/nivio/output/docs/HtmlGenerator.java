package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.output.LocalServer;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;


public abstract class HtmlGenerator {

    public abstract String toDocument(Landscape landscape);


    protected ContainerTag getHead(Landscape landscape) {
        return head(
                title(landscape.getName()),
                link().attr("rel", "stylesheet").attr("href", LocalServer.url("/css/bootstrap.min.css")),
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1, shrink-to-fit=no"),
                meta().attr("name", "description").attr("content", landscape.getName()),
                meta().attr("name", "author").attr("content", landscape.getContact()),
                meta().attr("generator", "author").attr("content", "nivio"),
                style("html {margin: 1rem} .group{margin-top: 1rem;} .card{margin-bottom: 1rem;}").attr("type", "text/css")
        );
    }

}
