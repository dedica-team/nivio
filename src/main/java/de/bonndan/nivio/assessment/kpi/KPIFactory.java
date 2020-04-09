package de.bonndan.nivio.assessment.kpi;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for custom and builtin KPIs.
 *
 *
 *
 */
public class KPIFactory extends JsonDeserializer<Map<String, KPI>> implements Serializable {

    @Override
    public Map<String, KPI> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> result = mapper.convertValue(node, new TypeReference<Map<String, Map<String, ?>>>() {
        });

        Map<String, KPI> kpis = new HashMap<>();
        result.forEach((s, params) -> {
            if (s.equals(HealthKPI.IDENTIFIER)) {
                kpis.put(HealthKPI.IDENTIFIER, mapper.convertValue(params, HealthKPI.class));
                return;
            }

            kpis.put(s, mapper.convertValue(params, CustomKPI.class));
        });

        return kpis;
    }
}
