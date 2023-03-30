package org.se.mac.blorksandbox.scanner.job;

import org.se.mac.blorksandbox.jobqueue.job.QueuedJob;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.function.UriBuilder;
import org.se.mac.blorksandbox.scanner.model.UrlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

public class FileScannerJob implements QueuedJob {
    private static final Logger logger = LoggerFactory.getLogger(FileScannerJob.class);

    private final long created;
    private final Long id;
    private String devicePath;

    private UUID deviceId;

    private UrlType urlType;

    private ScannerService service;

    private UriBuilder uriBuilder = new UriBuilder();

    public FileScannerJob(UUID deviceId, String devicePath, UrlType urlType, ScannerService service) {
        this.devicePath = devicePath;
        this.urlType = urlType;
        this.created = LocalDate.now().toEpochDay();
        this.id = generateId();
        this.service = service;
        this.deviceId = deviceId;
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public Callable<Integer> getTask() {
        return () -> {
            try {
                if (Objects.isNull(devicePath) || "".equals(devicePath)) {
                    throw new NullPointerException("Invalid path to scan");
                }
                service.scan(uriBuilder.apply(this.devicePath, this.urlType), this.deviceId);
                return QueuedJob.JOB_STATUS_DONE;
            } catch (RuntimeException e) {
                logger.error("Something went wrong", e);
                return QueuedJob.JOB_STATUS_ERROR;
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
