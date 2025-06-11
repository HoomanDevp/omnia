package ir.stts.bajet.log.elastic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ElasticLogEncoder extends EncoderBase<ILoggingEvent> {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private final String appName;

    public ElasticLogEncoder(String appName) {
        this.appName = appName;
    }

    @Override
    public byte[] headerBytes() {
        return null;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');

        appendJsonField(builder, "timestamp", FORMATTER.format(Instant.ofEpochMilli(event.getTimeStamp())));
        builder.append(String.format("\"@timestamp\":%d,", event.getTimeStamp()));

        appendJsonField(builder, "level", event.getLevel().toString());
        appendJsonField(builder, "logger", event.getLoggerName());
        appendJsonField(builder, "thread", event.getThreadName());
        appendJsonField(builder, "application", appName);

        Map<String, String> mdc = event.getMDCPropertyMap();
        appendJsonField(builder, "job-id", mdc.getOrDefault("msg-id", "0"));
        appendJsonField(builder, "msg-id", mdc.getOrDefault("job-id", "0"));

        // user-info and client-info must be raw JSON
        appendJsonField(builder, "user-info", mdc.getOrDefault("user-info", "{}"), true, true);
        appendJsonField(builder, "client-info", mdc.getOrDefault("client-info", "{}"), false, true);

        String message = event.getFormattedMessage();
        if (message != null && message.startsWith("{") && message.endsWith("}")) {
            builder.append(",\"context\":").append(message);
        }

        builder.append("}\n");
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] footerBytes() {
        return null;
    }

    // ---------- Helper Methods ----------

    private void appendJsonField(StringBuilder builder, String key, String value) {
        appendJsonField(builder, key, value, true, false);
    }

    private void appendJsonField(StringBuilder builder, String key, String value, boolean withComma, boolean isRawJson) {
        builder.append("\"").append(key).append("\":");

        if (isRawJson && isValidJson(value)) {
            builder.append(value); // insert raw JSON (no quotes)
        } else {
            builder.append("\"").append(escapeJson(value)).append("\"");
        }

        if (withComma) {
            builder.append(",");
        }
    }

    private boolean isValidJson(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        String trimmed = value.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
