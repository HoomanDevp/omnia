package com.omnia.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.omnia.log.config.LogConfig;
import lombok.SneakyThrows;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.omnia.log.config.LogConfig.REGULAR_DEPTH;

public class LogSpec {

    private static final String LOG_KEY = "LOG";
    private static final String DATA_KEY = "DATA";
    private static final String EXCEPTION_KEY = "EXCEPTION";
    private static final String EXCEPTION_MSG_KEY = "EXCEPTION_MESSAGE";

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper() {{
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        registerModule(simpleModule);
    }};

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
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put(LOG_KEY, message);
        objectNode.put(EXCEPTION_MSG_KEY, e.getMessage());

        // Convert stack trace to an array of strings
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        String stackTrace = sw.toString();
        objectNode.put(EXCEPTION_KEY, stackTrace);

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
        maskSensitiveFields(node, depth, "", LogConfig.SENSITIVE_FIELDS, LogConfig.SENSITIVE_PATHS);
    }

    private static void maskSensitiveFields(JsonNode node, int depth, String currentPath, Set<String> fields, Set<String> paths) {
        if (node == null || depth <= 0) return;

        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            objNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                String keyLower = key.toLowerCase();
                JsonNode value = entry.getValue();

                // Compute full path for path-based masking
                String fullPath = currentPath.isEmpty() ? key : currentPath + "." + key;

                // 1️⃣ Path-based masking
                if (paths.contains(fullPath)) {
                    objNode.put(key, LogConfig.MASK);
                    return; // skip recursion for masked node
                }

                // 2️⃣ Field-based masking
                if (fields.contains(keyLower)) {
                    objNode.put(key, LogConfig.MASK);
                } else {
                    // Recurse for nested objects/arrays
                    maskSensitiveFields(value, depth - 1, fullPath, fields, paths);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode item = arrayNode.get(i);
                // For arrays, include index in path if you want path precision; otherwise reuse currentPath
                maskSensitiveFields(item, depth - 1, currentPath, fields, paths);
            }
        }
    }

}