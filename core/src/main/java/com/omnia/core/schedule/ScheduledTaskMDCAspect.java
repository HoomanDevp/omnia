package com.omnia.core.schedule;

import com.omnia.core.header.constant.HeaderKey;
import com.omnia.core.uniqueref.JobIdGenerator;
import com.omnia.core.uniqueref.MessageIdGenerator;
import com.omnia.log.AppLogger;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@RequiredArgsConstructor
public class ScheduledTaskMDCAspect {
    private final AppLogger appLogger = new AppLogger(ScheduledTaskMDCAspect.class);
    private final JobIdGenerator jobIdGenerator;
    private final MessageIdGenerator messageIdGenerator;

    /**
     * Aspect advice that wraps execution of all scheduled tasks, injecting unique job and message IDs into the MDC,
     * logging task start, completion (with duration), and errors using the internal AppLogger.
     * Ensures MDC is cleared after execution.
     *
     * @param pjp the join point representing the scheduled method execution
     * @return the result of the scheduled method execution
     * @throws Throwable if the scheduled method throws any exception
     */
    @Around("execution(@org.springframework.scheduling.annotation.Scheduled * com.omnia..*(..))")
    public Object aroundScheduledTasks(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        MDC.put(HeaderKey.JOB_ID.getKey(), jobIdGenerator.generateId());
        MDC.put(HeaderKey.MSG_ID.getKey(), messageIdGenerator.generateId());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start(methodName);
        appLogger.infoF("Scheduled task started. method={}", methodName);
        try {
            Object result = pjp.proceed();
            stopWatch.stop();
            appLogger.infoF("Scheduled task finished. method={} duration={}ms", methodName, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Throwable ex) {
            appLogger.errorF("Scheduled task failed. method={} error={}", methodName, ex.getMessage());
            throw ex;
        } finally {
            MDC.clear();
        }
    }
}