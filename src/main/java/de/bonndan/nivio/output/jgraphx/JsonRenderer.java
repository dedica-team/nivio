package de.bonndan.nivio.output.jgraphx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.jgraphx.dto.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class JsonRenderer implements Renderer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRenderer.class);

    private final Renderer<mxGraph> mxGraphRenderer;

    public JsonRenderer(Renderer<mxGraph> mxGraphRenderer) {
        this.mxGraphRenderer = mxGraphRenderer;
    }

    @Override
    public String render(Landscape landscape) {
        mxGraph graph = mxGraphRenderer.render(landscape);

        List<Service> services = landscape.getServices();

        //this is to have the final layout
        mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);

        List<Serializable> dtos = new ArrayList<>();
        getAllChildren(dtos, graph, (mxCell) graph.getDefaultParent(), services);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to render json", e);
            throw new RuntimeException("Failed to render json", e);
        }
    }

    private void getAllChildren(List<Serializable> dtos, mxGraph graph, mxCell cell, List<Service> services) {
        dtos.addAll(
                Arrays.stream(graph.getChildCells(cell))
                        .map(o -> toDto((mxCell) o, services))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
        Arrays.stream(graph.getChildCells(cell)).forEach(o -> getAllChildren(dtos, graph, (mxCell) o, services));
    }

    private Serializable toDto(mxCell cell, List<Service> services) {

        mxGeometry geometry = cell.getGeometry();
        Map<String, String> style = parseStyle(cell.getStyle());

        ServiceItem serviceItem = null;
        if (!StringUtils.isEmpty(cell.getId()))
            serviceItem = ServiceItems.find(FullyQualifiedIdentifier.from(cell.getId()), services);
        Vertex vertex = new Vertex();
        vertex.id = cell.getId();
        vertex.name = (String) cell.getValue();
        vertex.type = style.get("type");
        vertex.group = style.get("group");
        vertex.groupColor = style.get("groupColor");
        if (cell.getParent().getGeometry() != null) {
            vertex.x = Math.round(geometry.getX() + cell.getParent().getGeometry().getX());
            vertex.y = Math.round(geometry.getY() + cell.getParent().getGeometry().getY());
        } else {
            vertex.x = Math.round(geometry.getX());
            vertex.y = Math.round(geometry.getY());
        }
        vertex.width = Math.round(geometry.getWidth());
        vertex.height = Math.round(geometry.getHeight());

        if (vertex.x == 0 && vertex.y == 0)
            return null;

        if (serviceItem != null) {
            vertex.service = serviceItem;
        }
        return vertex;
    }

    private Map<String, String> parseStyle(String style) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(StringUtils.delimitedListToStringArray(style, ";")).forEach(s -> {
            String[] keyvalue = StringUtils.delimitedListToStringArray(s, "=");
            if (StringUtils.isEmpty(s))
                return;
            if (keyvalue.length < 2)
                return;
            map.put(keyvalue[0], keyvalue[1]);
        });
        return map;
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {
        String json = render(landscape);
    }
}
