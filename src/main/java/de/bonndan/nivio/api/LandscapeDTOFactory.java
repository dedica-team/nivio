package de.bonndan.nivio.api;

import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderController;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static de.bonndan.nivio.output.map.JsonRenderController.MAP_JSON_ENDPOINT;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


public class LandscapeDTOFactory {

    public static LandscapeDTO from(Landscape item) {

        LandscapeDTO l = new LandscapeDTO();
        if (item == null)
            return l;

        l.identifier = item.getIdentifier();
        l.name = item.getName();
        l.contact = item.getContact();
        l.source = item.getSource();
        l.groups = item.getGroups();

        return l;
    }

    public static void addLinks(LandscapeDTO dto) {
        dto.add(linkTo(methodOn(ApiController.class).landscape(dto.getIdentifier()))
                .withSelfRel()
                .withMedia(MediaType.APPLICATION_JSON_VALUE)
        );
        dto.add(linkTo(methodOn(ApiController.class).items(dto.getIdentifier())).withRel("items"));
        dto.add(linkTo(methodOn(ApiController.class).reindex(dto.getIdentifier()))
                .withRel("reindex")
                .withMedia(MediaType.APPLICATION_JSON_VALUE)
                .withTitle("Reindex the source")
        );
        try {
            dto.add(linkTo(methodOn(JGraphXRenderController.class).pngResource(dto.getIdentifier()))
                    .withRel("png")
                    .withMedia(MediaType.IMAGE_PNG_VALUE)
                    .withTitle("Rendered Landscape")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //dto.add(linkTo(methodOn(DocsController.class).docResource(dto.getIdentifier())).withRel("docs"));
        dto.add(
                linkTo(methodOn(DocsController.class).htmlResource(dto.getIdentifier()))
                        .withRel("report")
                        .withTitle("Written landscape report")
        );
        dto.add(
                linkTo(methodOn(ApiController.class).log(dto.getIdentifier()))
                        .withRel("log")
                        .withMedia(MediaType.APPLICATION_JSON_VALUE)
                        .withTitle("Processing log")
        );

        String data = URLEncoder.encode(
                LocalServer.url("/render/" + dto.getIdentifier() + "/" + MAP_JSON_ENDPOINT),
                StandardCharsets.UTF_8
        );
        Link appLink = new Link(LocalServer.url("/app/index.html?data=" + data));
        dto.add(
                appLink.withRel("app")
                        .withMedia(MediaType.TEXT_HTML_VALUE)
                        .withTitle("Landscape map")
        );

    }
}
