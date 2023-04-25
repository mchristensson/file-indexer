package org.se.mac.blorksandbox.jobqueue;

import jakarta.annotation.PreDestroy;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Queue engine that processes tasks
 */
@Service
public class QueueService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
    public static final int EXECUTOR_DELAY = 5;
    public static final int CLEANUP_DELAY = 5;
    public static final int CLEANUP_PERIOD = 10;

    private final Map<Long, Integer> result;

    private ScheduledExecutorService executorService;

    public QueueService() {
        this(Executors.newScheduledThreadPool(2));
    }

    QueueService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
        this.result = new HashMap<>();
        initCleanupActivity();
    }


    @PreDestroy
    public void destroy() {
        logger.info("Shutting down executor service...");
        executorService.shutdown();
    }

    private void initCleanupActivity() {
        logger.debug("Init cleanup activity...");
        final Runnable cleanup = () -> {
            logger.info("Executing cleanup job...");
            int t = result.size();
            List<Long> keys = result.entrySet().stream()
                    .filter(longIntegerEntry -> longIntegerEntry.getValue() == QueuedJob.JOB_STATUS_DONE)
                    .map(Map.Entry::getKey).toList();
            int dt = keys.size();
            keys.forEach(result::remove);
            logger.info("Cleaned up {} of {}", dt, t);
        };
        executorService.scheduleAtFixedRate(cleanup, CLEANUP_DELAY, CLEANUP_PERIOD, TimeUnit.SECONDS);
    }

    /**
     * Enqueues a job
     *
     * @param job Job object to enqueue
     */
    public void enqueue(QueuedJob job) {
        result.put(job.getId(), QueuedJob.JOB_STATUS_RUNNING);
        try {
            Runnable wrapperAction = () -> {
                try {
                    this.result.put(job.getId(), job.getTask().call());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };
            executorService.schedule(wrapperAction, EXECUTOR_DELAY, TimeUnit.SECONDS);

        } catch (RejectedExecutionException e) {
            result.put(job.getId(), QueuedJob.JOB_STATUS_ERROR);
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return !executorService.isShutdown();
    }
    public Map<Long, Integer> getResult() {
        return new HashMap<>(this.result);
    }

}
