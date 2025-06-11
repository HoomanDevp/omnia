package com.omnia.log.elastic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;

@Getter
@Setter
public abstract class Worker extends Thread {

    private boolean secure = false;
    private String username;
    private String password;
    private int port = 9200;
    private String host = "http://localhost";

    private int bulkSize = 100;
    private String bulkPath = "/_bulk";
    private int flushIntervalInMillis = 500;

    private String index = "bajet-logs";
    private String indexTemplate = "bajet-logs-template";
    private String ilm = "bajet-logs-ilm";
//    private String dateTimeFormat = "yyyy/MM/dd HH:mm:ss";

    private int maxRetries = 3;
    private long retryDelayInMillis = 30000;

    private int queueSize = 256;
    private int maxFlushTime = 1000;
    private int discardingThreshold = 50;

    private Encoder<ILoggingEvent> encoder;
    private BlockingQueue<ILoggingEvent> queue;
}