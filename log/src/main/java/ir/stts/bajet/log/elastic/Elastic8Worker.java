package ir.stts.bajet.log.elastic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import okhttp3.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Elastic8Worker extends Worker {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .hostnameVerifier((hostname, session) -> true)
            .retryOnConnectionFailure(true)
            .build();

    @Override
    public void run() {
        try {
            applyElasticConfigIfNeeded();

            List<ILoggingEvent> batch = new ArrayList<>();
            long lastFlushTime = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                ILoggingEvent event = getQueue().poll(100, TimeUnit.MILLISECONDS);
                if (event != null && event.getLoggerName().startsWith("ir.stts.bajet")) {
                    batch.add(event);
                }

                if (shouldFlush(batch, lastFlushTime)) {
                    flushBatch(batch);
                    batch.clear();
                    lastFlushTime = System.currentTimeMillis();
                }
            }

            // Final flush
            if (!batch.isEmpty()) {
                flushBatch(batch);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace(); // Consider proper logging here
        }
    }

    private boolean shouldFlush(List<ILoggingEvent> batch, long lastFlushTime) {
        return !batch.isEmpty() && (
                batch.size() >= getBulkSize() ||
                        (System.currentTimeMillis() - lastFlushTime) >= getFlushIntervalInMillis()
        );
    }

    private void flushBatch(List<ILoggingEvent> batch) {
        String body = buildBulkRequestBody(batch);

        for (int attempt = 1; attempt <= getMaxRetries(); attempt++) {
            try {
                Request request = buildRequest(body);
                try (Response response = httpClient.newCall(request).execute()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful() || responseBody.contains("\"errors\":true")) {
                        throw new IOException("Elasticsearch error: " + response.code() + " - " + responseBody);
                    }
                    return; // Successful
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (attempt < getMaxRetries()) {
                    sleepWithBackoff(attempt);
                }
            }
        }
    }

    private String buildBulkRequestBody(List<ILoggingEvent> events) {
        StringBuilder bulk = new StringBuilder();
        String indexName = getIndex() + "-" + DATE_FORMATTER.format(LocalDate.now());

        for (ILoggingEvent event : events) {
            bulk.append("{\"index\": {\"_index\": \"").append(indexName).append("\"}}\n");
            bulk.append(new String(getEncoder().encode(event), StandardCharsets.UTF_8)).append("\n");
        }

        return bulk.toString();
    }

    private Request buildRequest(String body) {
        Request.Builder builder = new Request.Builder()
                .url(String.format("%s:%d%s", getHost(), getPort(), getBulkPath()))
                .post(RequestBody.create(body, JSON));

        if (isSecure()) {
            builder.addHeader("Authorization", "Basic " + getBasicAuthHeader());
        }

        return builder.build();
    }

    private void sleepWithBackoff(int attempt) {
        try {
            TimeUnit.MILLISECONDS.sleep(getRetryDelayInMillis() * attempt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getBasicAuthHeader() {
        String credentials = getUsername() + ":" + getPassword();
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private void applyElasticConfigIfNeeded() {
        try {
            applyIlmPolicy();
            applyIndexTemplate();
        } catch (IOException e) {
            throw new RuntimeException("Error applying Elasticsearch config", e);
        }
    }

    private void applyIlmPolicy() throws IOException {
        String ilmJson = readResourceFile("elk-log-ilm.json");
        Request ilmRequest = new Request.Builder()
                .url(String.format("%s:%d/_ilm/policy/%s", getHost(), getPort(), getIlm()))
                .put(RequestBody.create(ilmJson, JSON))
                .build();

        try (Response response = httpClient.newCall(ilmRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to apply ILM: " + response.code() + " - " + response.body().string());
            }
        }
    }

    private void applyIndexTemplate() throws IOException {
        String templateJson = readResourceFile("elk-log-index-template.json");
        Request templateRequest = new Request.Builder()
                .url(String.format("%s:%d/_index_template/%s", getHost(), getPort(), getIndexTemplate()))
                .put(RequestBody.create(templateJson, JSON))
                .build();

        try (Response response = httpClient.newCall(templateRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to apply index template: " + response.code() + " - " + response.body().string());
            }
        }
    }

    private String readResourceFile(String fileName) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:ir/stts/bajet/**/" + fileName);
        if (resources.length == 0) {
            throw new IOException("Resource not found: " + fileName);
        }
        return new String(resources[0].getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
