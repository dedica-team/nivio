package de.bonndan.nivio.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.bonndan.nivio.model.LinkedWrapper;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class HateoasSerializer extends JsonSerializer<LinkedWrapper> {

    private final LinkFactory linkFactory;

    public HateoasSerializer(LinkFactory linkFactory) {
        this.linkFactory = linkFactory;
    }

    @Override
    public void serialize(LinkedWrapper links, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeObject( linkFactory.getLinks(links.getComponent()));
    }

}