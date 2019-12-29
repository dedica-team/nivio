package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.map.*;

import java.util.concurrent.atomic.AtomicInteger;

public class RenderedXYMapFactory implements MapFactory<mxGraph, mxCell> {

    public RenderedXYMap getRenderedMap(Rendered<mxGraph, mxCell> rendered) {
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

        renderedMap.width = maxX.get() - minX.get();
        renderedMap.height = maxY.get() - minY.get();
        int size = Math.max(renderedMap.width, renderedMap.height) / 40;

        AtomicInteger minQ = new AtomicInteger(0);
        AtomicInteger maxQ = new AtomicInteger(0);
        AtomicInteger minR = new AtomicInteger(0);
        AtomicInteger maxR = new AtomicInteger(0);

        renderedMap.items.forEach(item -> {
            item.size = size;
            Hex hex = item.getHex();
            if (hex.q < minQ.get())
                minQ.set(hex.q);
            if (hex.q > maxQ.get())
                maxQ.set(hex.q);
            if (hex.r < minR.get())
                minR.set(hex.r);
            if (hex.r > maxR.get())
                maxR.set(hex.r);
            item.size = size;
        });

        renderedMap.minQ = minQ.get();
        renderedMap.maxQ = maxQ.get();
        renderedMap.minR = minR.get();
        renderedMap.maxR = maxR.get();
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
            renderedMap.items.add(new ItemMapItem(item, "", x, y, width, height));
        });

        rendered.getGroupObjects().forEach((group, cell) -> {
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
            renderedMap.groups.add(new GroupMapItem(group, x, y, width, height));
        });

        return renderedMap;
    }

}
