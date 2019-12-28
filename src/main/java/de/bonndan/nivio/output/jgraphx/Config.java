package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.output.map.MapFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public MapFactory<mxGraph, mxCell> getMapFactory() {
        return new RenderedXYMapFactory();
    }
}
