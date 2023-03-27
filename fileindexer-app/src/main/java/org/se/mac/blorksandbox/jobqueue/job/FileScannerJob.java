package org.se.mac.blorksandbox.jobqueue.job;

import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.UrlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

public class FileScannerJob implements QueuedJob {
    private static final Logger logger = LoggerFactory.getLogger(FileScannerJob.class);

    private final long created;
    private final Long id;
    private String path;
    private UrlType urlType;

    private ScannerService service;

    public FileScannerJob(String path, UrlType urlType, ScannerService service) {
        this.path = path;
        this.urlType = urlType;
        this.created = LocalDate.now().toEpochDay();
        this.id = generateId();
        this.service = service;
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public Callable<Integer> getTask() {
        return () -> {
            try {
                if (Objects.isNull(path) || "".equals(path)) {
                    throw new NullPointerException("Invalid path to scan");
                }
                service.scan(URI.create(path));
                return QueuedJob.JOB_STATUS_DONE;
            } catch (InterruptedException e) {
                logger.error("Something went wrong", e);
                throw new RejectedExecutionException("An error occurred when executing scanner task", e);
            } finally {
                logger.debug("Scanner job is all done!");
            }
        };
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
