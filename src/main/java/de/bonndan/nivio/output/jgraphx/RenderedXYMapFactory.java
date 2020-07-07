package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.RenderedArtifact;
import de.bonndan.nivio.output.map.MapFactory;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

public class RenderedXYMapFactory implements MapFactory<mxGraph, mxCell> {

    private final LocalServer localServer;

    public RenderedXYMapFactory(LocalServer localServer) {
        this.localServer = localServer;
    }

    public void applyArtifactValues(LandscapeImpl landscape, RenderedArtifact<mxGraph, mxCell> renderedArtifact) {

        applyValues(renderedArtifact);

        AtomicLong minX = new AtomicLong(0);
        AtomicLong maxX = new AtomicLong(0);
        AtomicLong minY = new AtomicLong(0);
        AtomicLong maxY = new AtomicLong(0);
        renderedArtifact.getItemObjects().forEach((item, v) -> {
            if (item.getX() < minX.get())
                minX.set(item.getX());
            if (item.getX() > maxX.get())
                maxX.set(item.getX());
            if (item.getY() < minY.get())
                minY.set(item.getY());
            if (item.getY() > maxY.get())
                maxY.set(item.getY());
        });

        landscape.setWidth(maxX.get() - minX.get());
        landscape.setHeight(maxY.get() - minY.get());
    }

    void applyValues(RenderedArtifact<mxGraph, mxCell> renderedArtifact) {


        renderedArtifact.getItemObjects().forEach((item, cell) -> {
            setRenderedLabels(item, getDimensionsFromCell(cell));
        });

        renderedArtifact.getGroupObjects().forEach((group, cell) -> {

            AtomicLong minX = new AtomicLong(Long.MAX_VALUE);
            AtomicLong maxX = new AtomicLong(Long.MIN_VALUE);
            AtomicLong minY = new AtomicLong(Long.MAX_VALUE);
            AtomicLong maxY = new AtomicLong(Long.MIN_VALUE);

            if (StringUtils.isEmpty(group.getColor())) {
                group.setColor(Color.getGroupColor(group));
            }
            group.getItems().forEach(landscapeItem -> {
                landscapeItem.setLabel(Rendered.LABEL_RENDERED_COLOR, group.getColor());
                mxCell mxCell = renderedArtifact.getItemObjects().get(landscapeItem);
                mxGeometry geometry = mxCell.getGeometry();
                long x = Math.round(geometry.getX());
                long y = Math.round(geometry.getY());

                if (x < minX.get()) minX.set(x);
                if (x > maxX.get()) maxX.set(x);
                if (y < minY.get()) minY.set(y);
                if (y > maxY.get()) maxY.set(y);
            });

            DimensionsFromCell dim = getDimensionsFromCell(cell);

            group.setX(dim.getX() + minX.get());
            group.setY(dim.getY() + minY.get());
            group.setWidth(maxX.get() - minX.get());
            group.setHeight(maxY.get() - minY.get());
        });

    }

    private void setRenderedLabels(Item item, DimensionsFromCell dim) {
        item.setLabel(Rendered.LABEL_RENDERED_ICON, localServer.getIconUrl(item).toString());
        item.setLabel(Rendered.LX, String.valueOf(dim.getX()));
        item.setLabel(Rendered.LY, String.valueOf(dim.getY()));
        item.setLabel(Rendered.LABEL_RENDERED_WIDTH, String.valueOf(dim.getWidth()));
        item.setLabel(Rendered.LABEL_RENDERED_HEIGHT, String.valueOf(dim.getHeight()));
    }

    private DimensionsFromCell getDimensionsFromCell(mxCell cell) {
        mxGeometry geometry = cell.getGeometry();
        long x;
        long y;
        if (cell.getParent().getGeometry() != null) {
            x = Math.round(geometry.getX() + cell.getParent().getGeometry().getX());
            y = Math.round(geometry.getY() + cell.getParent().getGeometry().getY());
        } else {
            x = Math.round(geometry.getX());
            y = Math.round(geometry.getY());
        }
        long width = Math.round(geometry.getWidth());
        long height = Math.round(geometry.getHeight());
        return new DimensionsFromCell(x, y, width, height);
    }

    private static class DimensionsFromCell {

        private final long x;
        private final long y;
        private final long width;
        private final long height;

        public DimensionsFromCell(long x, long y, long width, long height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public long getX() {
            return x;
        }

        public long getY() {
            return y;
        }

        public long getWidth() {
            return width;
        }

        public long getHeight() {
            return height;
        }
    }
}
