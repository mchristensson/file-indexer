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

public class FileHashAnalyzerJob extends FileScannerJob {
    private static final Logger logger = LoggerFactory.getLogger(FileHashAnalyzerJob.class);

    public FileHashAnalyzerJob(UUID deviceId, String devicePath, UrlType urlType, ScannerService service) {
        super(deviceId, devicePath, urlType, service);
    }

    @Override
    public Callable<Integer> getTask() {
        return () -> {
            try {
                if (Objects.isNull(devicePath) || "".equals(devicePath)) {
                    throw new NullPointerException("Invalid path to scan");
                }
                service.generateImageHash(uriBuilder.apply(this.devicePath, this.urlType), this.deviceId);
                return QueuedJob.JOB_STATUS_DONE;
            } catch (RuntimeException e) {
                logger.error("Something went wrong", e);
                return QueuedJob.JOB_STATUS_ERROR;
            } finally {
                logger.debug("Scanner job is all done!");
            }
        };
    }

}
