package de.bonndan.nivio.api;

import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.assessment.AssessmentController;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.map.MapController;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Factory that creates DTOs for the api.
 */
public class LandscapeDTOFactory {

    public static LandscapeDTO from(LandscapeImpl landscape) {

        LandscapeDTO landscapeDTO = new LandscapeDTO();
        if (landscape == null)
            return landscapeDTO;

        landscapeDTO.identifier = landscape.getIdentifier();
        landscapeDTO.name = landscape.getName();
        landscapeDTO.contact = landscape.getContact();
        landscapeDTO.source = landscape.getSource();
        landscapeDTO.description = landscape.getDescription();

        landscapeDTO.teams = landscape.getItems().stream()
                .map(item -> item.getLabel(Label.team))
                .filter(s -> !StringUtils.isEmpty(s))
                .collect(Collectors.toSet())
                .toArray(String[]::new);

        landscapeDTO.groups = getGroups(landscape);
        landscapeDTO.items = landscape.getItems().all();

        if (landscape.getLog() != null) {
            landscapeDTO.lastUpdate = landscape.getLog().getLastUpdate();
        }
        return landscapeDTO;
    }

    /**
     * This is mainly a type conversion.
     * @return groups by group identifier
     */
    private static Map<String, Group> getGroups(LandscapeImpl landscape) {
        Map<String, Group> groups = new HashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groups.put(s, (Group)groupItem));
        return groups;
    }

    public static void addLinks(LandscapeImpl dto) {
        dto.add(getLandscapeLink(dto, IanaLinkRelations.SELF));
        dto.add(linkTo(methodOn(ApiController.class).reindex(dto.getIdentifier()))
                .withRel("reindex")
                .withMedia(MediaType.APPLICATION_JSON_VALUE)
                .withTitle("Reindex the source")
        );
        try {
            dto.add(linkTo(methodOn(MapController.class).pngResource(dto.getIdentifier()))
                    .withRel("png")
                    .withMedia(MediaType.IMAGE_PNG_VALUE)
                    .withTitle("Rendered Landscape")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        dto.add(
                linkTo(methodOn(MapController.class).svg(dto.getIdentifier()))
                        .withRel("map")
                        .withMedia("image/svg+xml")
                        .withTitle("SVG map")
        );
        dto.add(
                linkTo(methodOn(AssessmentController.class).landscape(dto.getIdentifier()))
                        .withRel("assessment")
                        .withMedia(MediaType.APPLICATION_JSON_VALUE)
                        .withTitle("Assessment")
        );
    }

    /**
     * Returns the self-link to a landscape.
     *
     *
     */
    public static Link getLandscapeLink(LandscapeImpl landscape, LinkRelation rel) {
        return WebMvcLinkBuilder.linkTo(methodOn(ApiController.class).landscape(landscape.getIdentifier()))
                .withRel(rel)
                .withTitle("JSON representation of " + landscape.getName())
                .withMedia(MediaType.APPLICATION_JSON_VALUE);
    }
}
