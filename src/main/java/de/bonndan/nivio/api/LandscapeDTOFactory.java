package de.bonndan.nivio.api;

import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderController;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


public class LandscapeDTOFactory {

    public static LandscapeDTO from(Landscape item) {

        LandscapeDTO l = new LandscapeDTO();
        if (item == null)
            return l;

        l.identifier = item.getIdentifier();
        l.name = item.getName();
        l.contact = item.getContact();
        l.source = item.getSource();

        return l;
    }

    public static void addLinks(LandscapeDTO dto) {
        dto.add(linkTo(methodOn(ApiController.class).landscape(dto.getIdentifier()))
                .withSelfRel()
                .withMedia(MediaType.APPLICATION_JSON_UTF8_VALUE)
        );
        dto.add(linkTo(methodOn(ApiController.class).items(dto.getIdentifier())).withRel("items"));
        dto.add(linkTo(methodOn(ApiController.class).reindex(dto.getIdentifier()))
                .withRel("reindex")
                .withMedia(MediaType.APPLICATION_JSON_UTF8_VALUE)
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
        dto.add(linkTo(methodOn(DocsController.class).htmlResource(dto.getIdentifier())).withRel("report"));
    }
}
