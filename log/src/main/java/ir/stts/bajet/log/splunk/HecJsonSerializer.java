package ir.stts.bajet.log.splunk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class HecJsonSerializer {

    static {
        KEYWORDS = MetadataTags.HEC_TAGS;
    }

    private static final Set<String> KEYWORDS;
    private final Map<String, Object> template = new LinkedHashMap<>();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public HecJsonSerializer(Map<String, String> metadata) {

        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            this.setValue(entry.getKey(), entry.getValue());
        }

    }

    private void setValue(String key, String value) {
        if (KEYWORDS.contains(key)) {
            this.template.put(key, value);
        } else {
            if (!this.template.containsKey("fields")) {
                this.template.put("fields", new HashMap<>());
            }

            Object fields = this.template.get("fields");
            if (fields instanceof Map) {
                ((Map) fields).put(key, value);
            }
        }

    }

    public String serialize(HttpEventCollectorEventInfo info) {
        Map<String, Object> event = new HashMap<>(this.template);
        event.put("time", String.format(Locale.US, "%.3f", info.getTime()));
        event.put("event", info);

        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
