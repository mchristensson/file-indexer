package org.se.mac.blorksandbox.scanner.job;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.se.mac.blorksandbox.jobqueue.job.EnqueableJob;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.function.UriBuilder;
import org.se.mac.blorksandbox.scanner.model.UrlType;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Task performing analysis of the disk content on a Device.
 */
@EnqueableJob(title = "DirectoryScanning")
public class DirectoryScannerJob implements QueuedJob {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryScannerJob.class);

    public static final String DEVICE_PATH = "devicePath";
    public static final String URL_TYPE = "urlType";
    public static final String DEVICE_ID = "deviceId";

    private ApplicationContext applicationContext;

    private final long created;
    private final Long id;
    protected String devicePath;
    protected UUID deviceId;
    protected UrlType urlType;
    protected ScannerService service;

    protected UriBuilder uriBuilder = new UriBuilder();

    public DirectoryScannerJob() {
        this.created = LocalDate.now().toEpochDay();
        this.id = generateId();
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.devicePath = properties.get(DEVICE_PATH);
        if (this.devicePath == null || this.devicePath.isBlank()) {
            throw new RuntimeException("Invalid device path. (Did you provide a valid value for " +
                    "key '" + DEVICE_PATH + "'?)");
        }
        try {
            this.urlType = UrlType.valueOf(properties.get(URL_TYPE));
        } catch (IllegalArgumentException e) {
            this.urlType = UrlType.UNDEFINED;
        } catch (NullPointerException e) {
            throw new RuntimeException("Invalid url type. (Did you provide a valid value for key " +
                    "'" + URL_TYPE + "'?)");
        }
        String deviceIdRaw = properties.get(DEVICE_ID);
        if (deviceIdRaw == null || deviceIdRaw.isBlank()) {
            throw new RuntimeException("Invalid device ID. (Did you provide a valid value for key" +
                    " '" + DEVICE_ID + "'?)");
        }
        try {
            this.deviceId = UUID.fromString(deviceIdRaw);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Invalid deviceID. (Did you provide a valid value for key " +
                    "'" + DEVICE_ID + "'?)");
        }

        if (this.applicationContext == null) {
            throw new RuntimeException("Invocation requires a valid applicationcontext to be " +
                    "present.  (Did you invoke setApplicationContext before this method?)");
        }
        this.service = this.applicationContext.getBean(ScannerService.class);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
