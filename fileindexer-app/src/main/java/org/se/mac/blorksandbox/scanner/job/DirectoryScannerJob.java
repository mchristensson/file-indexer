package org.se.mac.blorksandbox.scanner.job;

import org.se.mac.blorksandbox.jobqueue.job.EnqueableJob;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.function.UriBuilder;
import org.se.mac.blorksandbox.scanner.model.UrlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

@EnqueableJob(title = "DirectoryScanning")
public class DirectoryScannerJob implements QueuedJob {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryScannerJob.class);

    public static final String DEVICE_PATH = "devicePath";
    public static final String URL_TYPE = "urlType";
    public static final String DEVICE_ID = "deviceId";

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
    public void setProperties(ApplicationContext ctx, Map<String, String> properties) {
        this.devicePath = properties.get(DEVICE_PATH);
        try {
            this.urlType = UrlType.valueOf(properties.get(URL_TYPE));
        } catch (IllegalArgumentException e) {
            this.urlType  = UrlType.UNDEFINED;
        }
        this.service = ctx.getBean(ScannerService.class);
        this.deviceId = UUID.fromString(properties.get(DEVICE_ID));
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
