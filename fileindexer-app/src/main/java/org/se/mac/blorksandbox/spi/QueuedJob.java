package org.se.mac.blorksandbox.spi;

import org.se.mac.blorksandbox.jobqueue.job.EnqueableJob;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

@EnqueableJob
public interface QueuedJob {

    int JOB_STATUS_ERROR = -1;
    int JOB_STATUS_DONE = 1;
    int JOB_STATUS_RUNNING = 76;

    long getCreated();

    Callable<Integer> getTask();

    Long getId();

    /**
     * Generates a uniquie identifier (UUID) and provides it as long
     *
     * @return Identifier
     */
    default long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    void setProperties(ApplicationContext ctx, Map<String, String> properties);

}

