package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.output.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dld4eRenderer implements Renderer<String> {

    private static final Logger logger = LoggerFactory.getLogger(Dld4eRenderer.class);

    private Layouter layouter = new Layouter();
    private StringBuilder ymlSource;

    private Networks networks = new Networks();
    private Groups groups = new Groups();
    private Connections connections = new Connections();
    private Icons icons = new Icons();

    @Override
    public String render(LandscapeImpl landscape) {

        landscape.getItems().forEach(this::addService);
        landscape.getItems().forEach(this::addLinks);

        layouter.arrange(icons, groups);

        ymlSource = new StringBuilder("");
        ymlSource.append(
                new DiagramItem("diagram")
                        .set("fill", "\"snow\"")
                        .set("columns", layouter.getColumns())
                        .set("rows", Layouter.APPLICATION_LEVEL +1)
                        .set("gridLines", "false")
                        .set("gridPaddingInner", "0.25")
                        .set("groupPadding", "0.75")
        );
        ymlSource.append(
                new DiagramItem("title")
                        .set("color", "\"black\"")
                        .set("heightPercentage", 5)
                        .set("logoFill", "none")
                        .set("stroke", "lightgrey")
                        .set("subText", landscape.getIdentifier())
                        .set("text", landscape.getName())
                        .set("type", "bar")
        );
        ymlSource.append(
                new DiagramItem("service")
                        .referable()
                        .set("stroke", "none")
                        .set("color", "\"black\"")
                        .set("iconFill", "darkslategrey")
                        .set("iconStroke", "lightgrey")
                        .set("iconStrokeWidth", ".25")
                        .set("iconFamily", "\"" + IconFamily.AzureEnterprise.name + "\"")
                        .set("fill", "none")
                        .set("preserveWhite", "true")
        );
        ymlSource.append(
                new DiagramItem("group")
                        .referable()
                        .set("stroke", "darkslategrey")
                        .set("strokeWidth", ".5")
        );
        ymlSource.append(
                new DiagramItem("dataflow")
                        .referable()
                        .set("stroke", "darkslategrey")
                        .set("strokeWidth", ".5")
        );
        ymlSource.append(
                new DiagramItem("provides")
                        .referable()
                        .set("stroke", "darkslategrey")
                        .set("strokeWidth", ".5")
                        .set("strokeDashArray", "[3,3]")
        );


        ymlSource.append(icons);
        ymlSource.append(groups);
        ymlSource.append(connections);

        return ymlSource.toString();
    }


    @Override
    public void render(LandscapeImpl landscape, File file) throws IOException {
        render(landscape);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(ymlSource.toString());
        fileWriter.close();
    }

    private void addService(Item item) {
        logger.info("Adding service " + item + " to d4dle diagram");
        networks.add(item);
        groups.add(item);
        icons.add(item);

        item.getProvidedBy().forEach(this::addService);
    }

    private void addLinks(Item item) {
        item.getRelations(RelationType.DATAFLOW).forEach(flow -> connections.addDataflow(flow));
        item.getRelations(RelationType.PROVIDER).forEach(provider -> connections.addProvider(provider.getSource(), item));
    }

    private static class Networks {

        private List<String> networks = new ArrayList<>();

        public void add(Item item) {
            if (item.getNetworks() == null)
                return;
            item.getNetworks().forEach(s -> {
                if (!networks.contains(s)) {
                    networks.add(s);
                }
            });
        }
    }

}
