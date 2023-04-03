package org.se.mac.blorksandbox.jobqueue.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.random.RandomGenerator;

public class DummyJob implements QueuedJob {
    private static final Logger logger = LoggerFactory.getLogger(DummyJob.class);
    private final long created;
    private final Long id;
    public DummyJob() {
        this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.id = generateId();
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public Callable<Integer> getTask() {
        return () -> {
            logger.debug("Begin job");
            try {
                Thread.sleep(RandomGenerator.getDefault().nextLong(500, 5000));
                return QueuedJob.JOB_STATUS_DONE;
            } catch (InterruptedException e) {
                throw new RejectedExecutionException("An error occurred when executing task", e);
            } finally {
                logger.debug("Job is all done!");
            }
        };
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
