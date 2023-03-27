package org.se.mac.blorksandbox.jobqueue;

import org.se.mac.blorksandbox.jobqueue.job.QueuedJob;
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

    private final Map<Long, Integer> result = new HashMap<>();

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public QueueService() {
        initCleanupActivity();
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
        executorService.scheduleAtFixedRate(cleanup, 5, 10, TimeUnit.SECONDS);
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
            executorService.schedule(wrapperAction, 5, TimeUnit.SECONDS);

        } catch (RejectedExecutionException e) {
            result.put(job.getId(), QueuedJob.JOB_STATUS_ERROR);
            throw new RuntimeException(e);
        }
    }

    public int getStatus(final long id) {
        return result.getOrDefault(id, -1);
    }

    public Map<Long, Integer> getResult() {
        return new HashMap<>(this.result);
    }

}
