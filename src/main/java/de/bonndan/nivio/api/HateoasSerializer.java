package de.bonndan.nivio.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.bonndan.nivio.model.LinkedWrapper;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * A special serializer to turn {@link de.bonndan.nivio.model.Linked} into a collection of hateoas links.
 *
 *
 */
@JsonComponent
public class HateoasSerializer extends JsonSerializer<LinkedWrapper> {

    private final LinkFactory linkFactory;

    public HateoasSerializer(LinkFactory linkFactory) {
        this.linkFactory = linkFactory;
    }

    /**
     * @param links a wrapper around {@link de.bonndan.nivio.model.Linked}
     */
    @Override
    public void serialize(LinkedWrapper links, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeObject(linkFactory.getLinks(links.getComponent()));
    }

}