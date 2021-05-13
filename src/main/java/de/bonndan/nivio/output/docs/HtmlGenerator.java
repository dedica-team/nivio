package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.util.Objects;

import static j2html.TagCreator.*;


public abstract class HtmlGenerator {

    @NonNull
    protected final LocalServer localServer;

    @NonNull
    protected final IconService iconService;

    /**
     * Generates the HTML report.
     *
     * @param landscape    the landscape object
     * @param searchConfig configuration for the report
     * @return rendered html
     */
    public abstract String toDocument(@NonNull final Landscape landscape, @Nullable final SearchConfig searchConfig);

    protected HtmlGenerator(@NonNull final LocalServer localServer, @NonNull final IconService iconService) {
        this.localServer = Objects.requireNonNull(localServer);
        this.iconService = Objects.requireNonNull(iconService);
    }

    protected ContainerTag getHead(Landscape landscape) {

        URL css = localServer.getUrl("/css/bootstrap.min.css").orElse(null);
        return head(
                title(landscape.getName()),
                link().condAttr(css != null, "rel", "stylesheet").attr("href", css),
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1, shrink-to-fit=no"),
                meta().attr("name", "description").attr("content", landscape.getName()),
                meta().attr("name", "author").attr("content", landscape.getContact()),
                meta().attr("generator", "author").attr("content", "nivio"),
                style("html {margin: 1rem} .group{margin-top: 1rem;} .card{margin-bottom: 1rem;}").attr("type", "text/css")
        );
    }

}
