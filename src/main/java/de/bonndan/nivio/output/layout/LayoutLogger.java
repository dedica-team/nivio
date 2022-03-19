package de.bonndan.nivio.output.layout;

import com.google.common.util.concurrent.AtomicDouble;
import de.bonndan.nivio.output.map.svg.SvgTagCreator;
import j2html.tags.ContainerTag;
import org.slf4j.helpers.MessageFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Only for debugging purposes.
 */
class LayoutLogger {

    private final List<String> messages = new ArrayList<>();
    private final List<Wrap> locations = new ArrayList<>();

    public void debug(String msg, Object... args) {
        String message = MessageFormatter.arrayFormat(msg, args).getMessage();
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void recordLocations(double[][] centerLocations, double[] radiuses) {
        locations.add(new Wrap(centerLocations, radiuses));
    }

    public void dump(File out) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
            for (String s : messages) {
                writer.write(s + "\n");
            }
        }
    }

    private static class Wrap {
        final double[] radiuses;
        final double[][] centerLocations;

        Wrap(double[][] centerLocations, double[] radiuses) {
            this.radiuses = radiuses;
            this.centerLocations = new double[centerLocations.length][];
            for (int i = 0; i < centerLocations.length; i++) {
                this.centerLocations[i] = new double[2];
                this.centerLocations[i][0] = centerLocations[i][0];
                this.centerLocations[i][1] = centerLocations[i][1];
            }
        }
    }

    public void traceLocations(File out) throws IOException { //NOSONAR
        Map<Integer, List<String>> pathCoords = new HashMap<>();
        for (int i = 0; i < locations.get(0).centerLocations.length; i++) {
            pathCoords.put(i, new ArrayList<>());
        }
        AtomicDouble minX = new AtomicDouble(Double.MAX_VALUE);
        AtomicDouble minY = new AtomicDouble(Double.MAX_VALUE);
        AtomicDouble maxX = new AtomicDouble(Double.MIN_VALUE);
        AtomicDouble maxY = new AtomicDouble(Double.MIN_VALUE);
        locations.forEach(wrap -> {
            for (int i = 0; i < wrap.centerLocations.length; i++) {
                if (wrap.centerLocations[i][0] < minX.get())
                    minX.set(wrap.centerLocations[i][0]);
                if (wrap.centerLocations[i][1] < minY.get())
                    minY.set(wrap.centerLocations[i][1]);
                if (wrap.centerLocations[i][0] > maxX.get())
                    maxX.set(wrap.centerLocations[i][0]);
                if (wrap.centerLocations[i][1] > maxY.get())
                    maxY.set(wrap.centerLocations[i][1]);
                pathCoords.get(i).add(wrap.centerLocations[i][0] + " " + wrap.centerLocations[i][1]);
            }
        });
        List<ContainerTag> paths = pathCoords.values().stream()
                .map(strings -> SvgTagCreator.path()
                        .attr("d", "M " + String.join(" L ", strings))
                        .attr("stroke", "black")
                        .attr("stroke-width", 5)
                        .attr("fill", "none")
                )
                .collect(Collectors.toList());

        var endPoints = locations.get(locations.size() - 1).centerLocations;
        List<ContainerTag> points = Arrays.stream(endPoints)
                .map(doubles -> SvgTagCreator.circle()
                        .attr("cx", doubles[0])
                        .attr("cy", doubles[1])
                        .attr("r", 10)
                        .attr("fill", "red")
                )
                .collect(Collectors.toList());


        var radiuses = locations.get(locations.size() - 1).radiuses;
        List<ContainerTag> radiusCircles = new ArrayList<>();
        for (int i = 0, endPointsLength = endPoints.length; i < endPointsLength; i++) {
            double[] doubles = endPoints[i];
            ContainerTag attr = SvgTagCreator.circle()
                    .attr("cx", doubles[0])
                    .attr("cy", doubles[1])
                    .attr("r", radiuses[i])
                    .attr("fill", "red")
                    .attr("fill-opacity", "0.4");
            radiusCircles.add(attr);
        }

        double width = maxX.get() - minX.get();
        double height = maxY.get() - minY.get();
        ContainerTag svg = SvgTagCreator.svg()
                .attr("version", "1.1")
                .attr("xmlns", "http://www.w3.org/2000/svg")
                .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                .attr("width", width)
                .attr("height", height)
                .attr("viewBox", minX.get() + " " + minY.get() + " " + width + " " + height)
                .with(paths)
                .with(points)
                .with(radiusCircles);

        String content = svg.render();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
            writer.write(content);
        }
    }
}
