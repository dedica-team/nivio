package de.bonndan.nivio.input.external.springboot;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Map;

@JsonComponent
public class CompositeHealthDeserializer extends JsonDeserializer<JsonCompositeHealth> {

    @Override
    public JsonCompositeHealth deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
        TextNode status = (TextNode) treeNode.get("status");
        Map<String, JsonCompositeHealth> components = (Map<String, JsonCompositeHealth>) treeNode.get("components");
        Map<String, String> details = (Map<String, String>) treeNode.get("details");
        return new JsonCompositeHealth(status.toString(), components, details);
    }

}

