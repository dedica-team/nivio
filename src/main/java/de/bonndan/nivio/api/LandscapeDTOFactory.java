package de.bonndan.nivio.api;

import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.api.dto.LandscapeStatistics;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderController;
import de.bonndan.nivio.output.map.MapController;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


public class LandscapeDTOFactory {

    public static LandscapeDTO from(Landscape landscape) {

        LandscapeDTO l = new LandscapeDTO();
        if (landscape == null)
            return l;

        l.identifier = landscape.getIdentifier();
        l.name = landscape.getName();
        l.contact = landscape.getContact();
        l.source = landscape.getSource();
        l.description = landscape.getDescription();

        if (landscape instanceof LandscapeImpl) {
            l.stats = getLandscapeStats((LandscapeImpl) landscape);
        }

        return l;
    }

    private static LandscapeStatistics getLandscapeStats(LandscapeImpl impl) {

        LandscapeStatistics stats = new LandscapeStatistics();
        stats.items = impl.getItems().all().size();
        stats.groups = impl.getGroups().size();

        List<StatusItem> collect = impl.getItems().stream()
                .map(item -> StatusItem.highestOf(item.getStatuses())).flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            stats.overallStatus = collect.get(0).getStatus();
        }

        stats.teams = impl.getItems().stream()
                .map(Item::getTeam)
                .filter(s -> !StringUtils.isEmpty(s))
                .collect(Collectors.toSet())
                .toArray(String[]::new);

        if (impl.getLog() != null) {
            stats.lastUpdate = impl.getLog().getLastUpdate();
        }
        return stats;
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

    }
}
