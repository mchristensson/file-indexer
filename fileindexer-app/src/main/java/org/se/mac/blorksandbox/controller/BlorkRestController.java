package org.se.mac.blorksandbox.controller;

import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.jobqueue.QueueService;
import org.se.mac.blorksandbox.jobqueue.job.DummyJob;
import org.se.mac.blorksandbox.jobqueue.job.FileScannerJob;
import org.se.mac.blorksandbox.jobqueue.job.QueuedJob;
import org.se.mac.blorksandbox.jobqueue.rest.LogicalFileValues;
import org.se.mac.blorksandbox.jobqueue.rest.QueueJobStatus;
import org.se.mac.blorksandbox.jobqueue.rest.ScanRequestReceipt;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.UrlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api")
public class BlorkRestController {

    private static final Logger logger = LoggerFactory.getLogger(BlorkRestController.class);

    @Autowired
    private QueueService queueService;

    @Autowired
    private ScannerService scannerService;

    @Autowired
    private LogicalFileIndexService fileIndexService;

    @GetMapping("queue/enqueue")
    public ScanRequestReceipt enqueueDummyJob() {
        logger.debug("Enqueuing dummyjob...");
        QueuedJob scanJob = new DummyJob();
        queueService.enqueue(scanJob);
        return new ScanRequestReceipt(scanJob.getId(), "Job was enqueued");
    }

    @GetMapping(value = "queue/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public QueueJobStatus queueQueueJobStatus() {
        logger.debug("Retrieving job data from queue...");
        return new QueueJobStatus(queueService.getResult());
    }

    @GetMapping("scan/enqueue")
    public ScanRequestReceipt pollScan() {
        logger.debug("Enqueuing job...");
        QueuedJob scanJob = new FileScannerJob("file:///c:/temp", UrlType.WIN_DRIVE_LETTER, scannerService);
        queueService.enqueue(scanJob);
        return new ScanRequestReceipt(scanJob.getId(), "Scanner job was enqueued");
    }

    @GetMapping("scan/list")
    public LogicalFileValues listLogicalFiles() {
        logger.debug("Retrieving files from index...");
        final List<String> names =
                fileIndexService.getAll().stream()
                        .filter(Objects::nonNull)
                        .map(f -> f.getProperties().getOrDefault("name", "namn saknas bland properties")).toList();
        final LogicalFileValues result = new LogicalFileValues();
        result.setNames(names);
        return result;
    }
}
