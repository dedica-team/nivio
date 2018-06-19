package de.bonndan.nivio.output;

import org.jgrapht.Graph;

import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.io.DOTExporter;

public class GraphRenderer {

    public static void render(Graph g) throws IOException {
        DOTExporter exporter = new DOTExporter();
        String targetDirectory = "/tmp/";
        exporter.exportGraph(g, new FileWriter(targetDirectory + "initial-graph.dot"));
    }

}
