package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.map.GroupMapItem;
import de.bonndan.nivio.output.map.ItemMapItem;
import de.bonndan.nivio.output.map.MapFactory;
import de.bonndan.nivio.output.map.RenderedXYMap;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RenderedXYMapFactory implements MapFactory<mxGraph, mxCell> {

    private final LocalServer localServer;

    public RenderedXYMapFactory(LocalServer localServer) {
        this.localServer = localServer;
    }

    public RenderedXYMap getRenderedMap(LandscapeImpl landscape, Rendered<mxGraph, mxCell> rendered) {
        RenderedXYMap renderedMap = from(rendered);

        AtomicInteger minX = new AtomicInteger(0);
        AtomicInteger maxX = new AtomicInteger(0);
        AtomicInteger minY = new AtomicInteger(0);
        AtomicInteger maxY = new AtomicInteger(0);
        renderedMap.items.forEach(item -> {
            if (item.x < minX.get())
                minX.set((int) item.x);
            if (item.x > maxX.get())
                maxX.set((int) item.x);
            if (item.y < minY.get())
                minY.set((int) item.y);
            if (item.y > maxY.get())
                maxY.set((int) item.y);
        });

        renderedMap.landscape = landscape.getName();
        renderedMap.width = maxX.get() - minX.get();
        renderedMap.height = maxY.get() - minY.get();

        return renderedMap;
    }

    RenderedXYMap from(Rendered<mxGraph, mxCell> rendered) {
        RenderedXYMap renderedMap = new RenderedXYMap();

        rendered.getItemObjects().forEach((item, cell) -> {
            DimensionsFromCell dim = getDimensionsFromCell(cell);
            renderedMap.items.add(new ItemMapItem(item, localServer.getIconUrl(item).toString(), dim.getX(), dim.getY(), dim.getWidth(), dim.getHeight()));
        });

        rendered.getGroupObjects().forEach((group, cell) -> {

            AtomicLong minX = new AtomicLong(Long.MAX_VALUE);
            AtomicLong maxX = new AtomicLong(Long.MIN_VALUE);
            AtomicLong minY = new AtomicLong(Long.MAX_VALUE);
            AtomicLong maxY = new AtomicLong(Long.MIN_VALUE);

            group.getItems().forEach(landscapeItem -> {
                mxCell mxCell = rendered.getItemObjects().get(landscapeItem);
                mxGeometry geometry = mxCell.getGeometry();
                long x = Math.round(geometry.getX());
                long y = Math.round(geometry.getY());

                if (x < minX.get()) minX.set(x);
                if (x > maxX.get()) maxX.set(x);
                if (y < minY.get()) minY.set(y);
                if (y > maxY.get()) maxY.set(y);
            });

            DimensionsFromCell dim = getDimensionsFromCell(cell);

            renderedMap.groups.add(
                    new GroupMapItem(
                            group,
                            dim.getX() + minX.get(),
                            dim.getY() + minY.get(),
                            dim.getX() + maxX.get(),
                            dim.getY() + maxY.get()
                    )
            );
        });

        return renderedMap;
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

        private long x;
        private long y;
        private long width;
        private long height;

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
