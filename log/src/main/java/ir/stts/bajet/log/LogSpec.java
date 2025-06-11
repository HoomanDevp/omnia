package ir.stts.bajet.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ir.stts.bajet.log.config.LogConfig;
import lombok.SneakyThrows;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static ir.stts.bajet.log.config.LogConfig.REGULAR_DEPTH;

public class LogSpec {

    private static final String LOG_KEY = "LOG";
    private static final String DATA_KEY = "DATA";
    private static final String EXCEPTION_KEY = "EXCEPTION";
    private static final String EXCEPTION_MSG_KEY = "EXCEPTION_MESSAGE";
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    // 👇 Define sensitive keys you want to mask

    protected LogSpec() {
    }

    @SneakyThrows
    public static ObjectNode ofData(String message, Object data) {

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put(LOG_KEY, message);
        objectNode.set(DATA_KEY, OBJECT_MAPPER.valueToTree(data));
        maskSensitiveFields(objectNode, REGULAR_DEPTH);
        return objectNode;
    }

    @SneakyThrows
    public static ObjectNode ofMap(String message, Map<String, Object> entries) {

        return ofData(message, entries);
    }

    @SneakyThrows
    public static ObjectNode ofException(String message, Throwable e) {
        StringWriter sw = new StringWriter(512);
        e.printStackTrace(new PrintWriter(sw));
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put(LOG_KEY, message);
        objectNode.put(EXCEPTION_MSG_KEY, e.getMessage());
        objectNode.put(EXCEPTION_KEY, sw.toString());
        maskSensitiveFields(objectNode, REGULAR_DEPTH);
        return objectNode;
    }

    public static ObjectNode ofMessage(String message, String... messages) {

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put(LOG_KEY, messages != null && messages.length > 0
                ? message + " " + String.join(" ", messages)
                : message);
        maskSensitiveFields(objectNode, REGULAR_DEPTH);
        return objectNode;
    }

    public static ObjectNode ofMessage(char delimiter, String message, String... messages) {

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put(LOG_KEY, messages != null && messages.length > 0
                ? message + delimiter + String.join(delimiter + "", messages)
                : message);
        maskSensitiveFields(objectNode, REGULAR_DEPTH);
        return objectNode;
    }

    protected static void maskSensitiveFields(JsonNode node, int depth) {
        if (LogConfig.SENSITIVE_FIELDS.isEmpty()) {
            return;
        }

        if (depth <= 0) {
            return; // Stop processing if the maximum depth is reached
        }

        if (node == null) return;

        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                if (LogConfig.SENSITIVE_FIELDS.contains(entry.getKey().toLowerCase())) {
                    objectNode.put(entry.getKey(), LogConfig.MASK);
                } else {
                    maskSensitiveFields(entry.getValue(), depth - 1);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.forEach(jsonNode -> maskSensitiveFields(jsonNode, depth - 1));
        }
    }

}