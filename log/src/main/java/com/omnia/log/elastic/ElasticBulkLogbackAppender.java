package com.omnia.log.elastic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.util.InterruptUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter
public class ElasticBulkLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final String indexPrefix = "omnia-logs";

    private boolean secure = false;
    private String username;
    private String password;
    private String appName;
    private String profile;
    private int port = 9200;
    private String host = "http://localhost";

    private int bulkSize = 100;
    private String bulkPath = "/_bulk";
    private int flushIntervalInMillis = 500;


    private int maxRetries = 3;
    private long retryDelayInMillis = 30000;

    private int queueSize = 256;
    private int maxFlushTime = 1000;
    private int discardingThreshold = -1;

    private BlockingQueue<ILoggingEvent> blockingQueue;
    private final Worker worker = new Elastic8Worker();
    private Encoder<ILoggingEvent> encoder;

    protected boolean isDiscardable(ILoggingEvent eventObject) {
        return false;
    }

    @Override
    public void start() {
        // app name has value here
        encoder = new ElasticLogEncoder(appName);
        if (isStarted())
            return;

        if (queueSize < 1) {

            addError("Invalid queue size [" + queueSize + "]");
            return;
        }

        blockingQueue = new ArrayBlockingQueue<>(queueSize);

        if (discardingThreshold == -1)
            discardingThreshold = queueSize / 5;
        addInfo("Setting discardingThreshold to " + discardingThreshold);
        worker.setDaemon(true);
        worker.setName("AsyncElkAppender-Worker-" + getName());

        worker.setSecure(secure);
        worker.setUsername(username);
        worker.setPassword(password);
        worker.setPort(port);
        worker.setHost(host);

        worker.setBulkSize(bulkSize);
        worker.setBulkPath(bulkPath);
        worker.setFlushIntervalInMillis(flushIntervalInMillis);

        worker.setIndex(indexPrefix + "-" + appName + "-" + profile);

        worker.setMaxRetries(maxRetries);
        worker.setRetryDelayInMillis(retryDelayInMillis);

        worker.setQueueSize(queueSize);
        worker.setMaxFlushTime(maxFlushTime);
        worker.setDiscardingThreshold(discardingThreshold);

        worker.setEncoder(encoder);
        worker.setQueue(blockingQueue);

        super.start();
        worker.start();
    }

    @Override
    public void stop() {

        if (!isStarted())
            return;

        super.stop();

        worker.interrupt();
        InterruptUtil interruptUtil = new InterruptUtil(context);

        try {

            interruptUtil.maskInterruptFlag();
            worker.join(maxFlushTime);

            if (worker.isAlive())
                addWarn("Max queue flush timeout (" + maxFlushTime + " ms) exceeded. Approximately " + blockingQueue.size() + " queued events were possibly discarded.");
            else
                addInfo("Queue flush finished successfully within timeout.");
        } catch (InterruptedException e) {

            int remaining = blockingQueue.size();
            addError("Failed to join worker thread. " + remaining + " queued events may be discarded.", e);
        } finally {

            interruptUtil.unmaskInterruptFlag();
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {

        if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject))
            return;

        put(eventObject);
    }

    public boolean isQueueBelowDiscardingThreshold() {
        return (blockingQueue.remainingCapacity() < discardingThreshold);
    }

    private void put(ILoggingEvent eventObject) {
        putUninterruptibly(eventObject);
    }

    private void putUninterruptibly(ILoggingEvent eventObject) {

        boolean interrupted = false;
        try {
            while (true)
                try {
                    blockingQueue.put(eventObject);
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
        } finally {
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }
}