package com.omnia.log;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class AppLogger {
    private final Logger logger;

    public AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    private ObjectNode format(String format, Object... args) {
        return LogSpec.ofMessage(MessageFormatter.arrayFormat(format, args).getMessage());
    }

    // Info Level Logging
    public void info(ObjectNode logSpec) {
        logger.info("{}", logSpec);
    }

    public void info(String message, String... messages) {
        logger.info("{}", LogSpec.ofMessage(message, messages));
    }

    public void info(String message) {
        logger.info("{}", LogSpec.ofMessage(message));
    }

    public void info(String message, Object data) {
        logger.info("{}", LogSpec.ofData(message, data));
    }

    public void infoF(String pattern, Object... args) {
        logger.info("{}", format(pattern, args));
    }

    public void info(Object data) {
        logger.info("{}", LogSpec.ofData(null, data));
    }

    public void info(String message, Exception e) {
        logger.info("{}", LogSpec.ofException(message, e));
    }

    public void info(Exception e) {
        logger.info("{}", LogSpec.ofException(null, e));
    }

    // Warn Level Logging
    public void warn(String message, String... messages) {
        logger.info("{}", LogSpec.ofMessage(message, messages));
    }

    public void warn(ObjectNode logSpec) {
        logger.warn("{}", logSpec);
    }

    public void warnF(String pattern, Object... args) {
        logger.warn("{}", format(pattern, args));
    }

    public void warn(String message, Object data) {
        logger.warn("{}", LogSpec.ofData(message, data));
    }

    public void warn(String message) {
        logger.warn("{}", LogSpec.ofMessage(message));
    }

    public void warn(Object data) {
        logger.warn("{}", LogSpec.ofData(null, data));
    }

    public void warn(String message, Exception e) {
        logger.warn("{}", LogSpec.ofException(message, e));
    }

    public void warn(Exception e) {
        logger.warn("{}", LogSpec.ofException(null, e));
    }

    // Error Level Logging
    public void error(ObjectNode logSpec) {
        logger.error("{}", logSpec);
    }

    public void error(String message, Object data) {
        logger.error("{}", LogSpec.ofData(message, data));
    }

    public void errorF(String pattern, Object... args) {
        logger.error("{}", format(pattern, args));
    }

    public void error(String message) {
        logger.error("{}", LogSpec.ofMessage(message));
    }

    public void error(Object data) {
        logger.error("{}", LogSpec.ofData(null, data));
    }

    public void error(String message, Exception e) {
        logger.error("{}", LogSpec.ofException(message, e));
    }

    public void error(Exception e) {
        logger.error("{}", LogSpec.ofException(null, e));
    }

    public void error(String message, String... messages) {
        logger.error("{}", LogSpec.ofMessage(message, messages));
    }

    // Debug Level Logging
    public void debug(ObjectNode logSpec) {
        logger.debug("{}", logSpec);
    }

    public void debug(String message, Object data) {
        logger.debug("{}", LogSpec.ofData(message, data));
    }

    public void debug(String message) {
        logger.debug("{}", LogSpec.ofMessage(message));
    }

    public void debug(Object data) {
        logger.debug("{}", LogSpec.ofData(null, data));
    }

    public void debug(String message, Exception e) {
        logger.debug("{}", LogSpec.ofException(message, e));
    }

    public void debugF(String pattern, Object... args) {
        logger.debug("{}", format(pattern, args));
    }

    public void debug(String message, String... messages) {
        logger.debug("{}", LogSpec.ofMessage(message, messages));
    }

    public void debug(Exception e) {
        logger.debug("{}", LogSpec.ofException(null, e));
    }


}
