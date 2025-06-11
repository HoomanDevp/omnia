package ir.stts.bajet.core.resilience.handler;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.resilience.exception.BajetException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ErrorMonitoring {

    private ScheduledExecutorService scheduler;

    private AtomicLong totalErrors;
    private Map<String, Long> lastLogTimeMap;
    private Map<String, Integer> errorThresholdsMap;
    private Map<String, Integer> errorTimeBoxInMillisMap;
    private Map<String, Queue<Long>> errorTimestampsMap;

    private volatile long notificationIntervalInMillis;

    private final MeterRegistry meterRegistry;
    private final Map<String, Boolean> errorFlags = new HashMap<>();

    public ErrorMonitoring(MeterRegistry meterRegistry) {

        this.meterRegistry = meterRegistry;

        this.init();
        this.start();
    }

    public boolean registerError(BajetException error) {

        if (this.scheduler.isShutdown())
            return false;

        String errorCode = error.getErrorCode();
        errorThresholdsMap.put(errorCode, error.getThreshold());
        int timeBoxInMillis = error.getTimeBoxInMinutes() * 60_000;
        errorTimeBoxInMillisMap.put(errorCode, timeBoxInMillis);
        lastLogTimeMap.putIfAbsent(errorCode, 0L);
        errorTimestampsMap.putIfAbsent(errorCode, new ConcurrentLinkedQueue<>());

        Queue<Long> errorTimestamps = errorTimestampsMap.get(errorCode);
        long currentTime = System.currentTimeMillis();
        errorTimestamps.offer(currentTime);

        totalErrors.incrementAndGet();
        adjustNotificationInterval();

        if (!errorFlags.containsKey(errorCode)) {

            errorFlags.put(errorCode, false);
            Gauge.builder(BajetConstants.ERROR_LIVE_THRESHOLD_GAUGE, errorCode, code -> errorFlags.get(code) ? 1 : 0)
                    .description("Indicates whether the error flag is enabled (1) or disabled (0)")
                    .tags(error.tags())
                    .register(meterRegistry);

        }

        return true;
    }

    void start() {

        if (scheduler != null && !scheduler.isShutdown())
            scheduler.shutdown();

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::processErrors, 1, 1, TimeUnit.SECONDS);
    }

    void shutdown() {

        scheduler.shutdown();
        this.init();
    }

    private void init() {

        this.notificationIntervalInMillis = 60_000L;
        this.totalErrors = new AtomicLong(0);
        this.lastLogTimeMap = new ConcurrentHashMap<>();
        this.errorThresholdsMap = new ConcurrentHashMap<>();
        this.errorTimeBoxInMillisMap = new ConcurrentHashMap<>();
        this.errorTimestampsMap = new ConcurrentHashMap<>();

        Gauge.builder(BajetConstants.ERROR_LIVE_GAUGE, () -> totalErrors.get())
                .description("The current number of live errors being tracked in the application")
                .register(meterRegistry);
    }

    private void processErrors() {

        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Queue<Long>> entry : errorTimestampsMap.entrySet()) {

            String errorCode = entry.getKey();
            Queue<Long> errorTimestamps = entry.getValue();
            long errorCount = errorTimestamps.size();

            long lastLogTime = lastLogTimeMap.getOrDefault(errorCode, 0L);
            if (currentTime - lastLogTime >= notificationIntervalInMillis) {

                Integer timeBoxInMillis = errorTimeBoxInMillisMap.get(errorCode);
                int threshold = errorThresholdsMap.getOrDefault(errorCode, Integer.MAX_VALUE);
                if (errorCount > threshold) {

                    lastLogTimeMap.put(errorCode, currentTime);
                    errorFlags.put(errorCode, true);
                } else
                    errorFlags.put(errorCode, false);

                while (!errorTimestamps.isEmpty() && currentTime - errorTimestamps.peek() > timeBoxInMillis) {

                    errorTimestamps.poll();
                    totalErrors.decrementAndGet();
                }
            }
        }
    }

    private void adjustNotificationInterval() {

        long errors = totalErrors.get();

        if (errors < 100)
            notificationIntervalInMillis = 60_000L;
        else if (errors < 300)
            notificationIntervalInMillis = 45_000L;
        else if (errors < 500)
            notificationIntervalInMillis = 30_000L;
        else if (errors < 800)
            notificationIntervalInMillis = 25_000L;
        else if (errors < 900)
            notificationIntervalInMillis = 20_000L;
        else if (errors < 1000)
            notificationIntervalInMillis = 15_000L;
        else
            notificationIntervalInMillis = 10_000L;
    }
}