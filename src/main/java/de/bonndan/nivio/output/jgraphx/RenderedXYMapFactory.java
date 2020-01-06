package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.map.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RenderedXYMapFactory implements MapFactory<mxGraph, mxCell> {

    public static final int SIZE_FACTOR = 40;
    private final IconService iconService;

    public RenderedXYMapFactory(IconService iconService) {
        this.iconService = iconService;
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
        renderedMap.sizeFactor = SIZE_FACTOR;
        renderedMap.width = maxX.get() - minX.get();
        renderedMap.height = maxY.get() - minY.get();
        int size = Math.max(renderedMap.width, renderedMap.height) / SIZE_FACTOR;

        renderedMap.items.forEach(item -> item.size = size);
        renderedMap.groups.forEach(groupMapItem -> groupMapItem.size = size);

        return renderedMap;
    }

    RenderedXYMap from(Rendered<mxGraph, mxCell> rendered) {
        RenderedXYMap renderedMap = new RenderedXYMap();

        rendered.getItemObjects().forEach((item, cell) -> {
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

            renderedMap.items.add(new ItemMapItem(item, iconService.getIcon(item).getUrl().toString(), x, y, width, height));
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

            renderedMap.groups.add(
                    new GroupMapItem(
                            group,
                            x + minX.get(),
                            y + minY.get(),
                            x + maxX.get(),
                            y + maxY.get()
                    )
            );
        });

        return renderedMap;
    }

}
