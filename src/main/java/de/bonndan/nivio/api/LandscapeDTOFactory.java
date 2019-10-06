package de.bonndan.nivio.api;

import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderController;

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
        l.stateProviders = item.getStateProviders();
        l.source = item.getSource();

        return l;
    }

    public static void addLinks(LandscapeDTO dto) {
        dto.add(linkTo(methodOn(ApiController.class).landscape(dto.getIdentifier())).withSelfRel());
        dto.add(linkTo(methodOn(ApiController.class).items(dto.getIdentifier())).withRel("items"));
        dto.add(linkTo(methodOn(ApiController.class).reindex(dto.getIdentifier())).withRel("reindex"));
        try {
            dto.add(linkTo(methodOn(JGraphXRenderController.class).pngResource(dto.getIdentifier())).withRel("png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //dto.add(linkTo(methodOn(DocsController.class).docResource(dto.getIdentifier())).withRel("docs"));
        dto.add(linkTo(methodOn(DocsController.class).htmlResource(dto.getIdentifier())).withRel("report"));
    }
}
