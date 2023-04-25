package org.se.mac.blorksandbox.scanner.job;

import org.se.mac.blorksandbox.jobqueue.job.EnqueableJob;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Callable;

@EnqueableJob(title = "Image File Hash Generator")
public class ImageFileHashGeneratorJob extends DirectoryScannerJob {

    private static final Logger logger = LoggerFactory.getLogger(ImageFileHashGeneratorJob.class);

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
