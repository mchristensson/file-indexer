package org.se.mac.blorksandbox.controller;

import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.jobqueue.QueueService;
import org.se.mac.blorksandbox.jobqueue.job.DummyJob;
import org.se.mac.blorksandbox.rest.LogicalFilesSearchResult;
import org.se.mac.blorksandbox.scanner.job.FileHashAnalyzerJob;
import org.se.mac.blorksandbox.scanner.job.FileScannerJob;
import org.se.mac.blorksandbox.jobqueue.job.QueuedJob;
import org.se.mac.blorksandbox.scanner.rest.CompareHashPairRequest;
import org.se.mac.blorksandbox.scanner.rest.LogicalFileValue;
import org.se.mac.blorksandbox.jobqueue.rest.QueueJobStatus;
import org.se.mac.blorksandbox.scanner.rest.ScanEnqueueReceipt;
import org.se.mac.blorksandbox.scanner.rest.ScanEnqueueRequest;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "api", method = {RequestMethod.GET, RequestMethod.POST})
public class BlorkRestController {

    private static final Logger logger = LoggerFactory.getLogger(BlorkRestController.class);

    @Autowired
    private QueueService queueService;

    @Autowired
    private ScannerService scannerService;

    @Autowired
    private LogicalFileIndexService fileIndexService;

    @GetMapping("queue/enqueue")
    public ScanEnqueueReceipt enqueueDummyJob() {
        logger.debug("Enqueuing dummyjob...");
        QueuedJob scanJob = new DummyJob();
        queueService.enqueue(scanJob);
        return new ScanEnqueueReceipt(scanJob.getId(), "Job was enqueued");
    }

    @GetMapping(value = "queue/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public QueueJobStatus queueQueueJobStatus() {
        logger.debug("Retrieving job data from queue...");
        return new QueueJobStatus(queueService.getResult());
    }

    @PostMapping(value = "scan/enqueue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ScanEnqueueReceipt pollScan(@RequestBody ScanEnqueueRequest scanEnqueueRequest) {
        logger.debug("Enqueuing job...");
        QueuedJob scanJob = new FileScannerJob(scanEnqueueRequest.getDeviceIdAsUUID(), scanEnqueueRequest.getPath(), scanEnqueueRequest.getUrlType(), scannerService);
        queueService.enqueue(scanJob);
        return new ScanEnqueueReceipt(scanJob.getId(), "Scanner job was enqueued");
    }

    @PostMapping(value = "imgash/enqueue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ScanEnqueueReceipt pollImageHashGeneration(@RequestBody ScanEnqueueRequest scanEnqueueRequest) {
        logger.debug("Enqueuing job...");
        QueuedJob scanJob = new FileHashAnalyzerJob(scanEnqueueRequest.getDeviceIdAsUUID(), scanEnqueueRequest.getPath(), scanEnqueueRequest.getUrlType(), scannerService);
        queueService.enqueue(scanJob);
        return new ScanEnqueueReceipt(scanJob.getId(), "Scanner job was enqueued");
    }

    @GetMapping("scan/list")
    public LogicalFilesSearchResult listLogicalFiles() {
        logger.debug("Retrieving files from index...");
        final List<LogicalFileValue> names =
                fileIndexService.getAllFiles().stream()
                        .filter(Objects::nonNull)
                        .map(transformLogicalFile()).toList();
        return new LogicalFilesSearchResult(names.toArray(new LogicalFileValue[0]));
    }

    @GetMapping("imgash/list")
    public LogicalFilesSearchResult listImageHash() {
        logger.debug("Retrieving hashes from index...");
        final List<LogicalFileValue> names =
                fileIndexService.getAllFileHashes().stream()
                        .filter(Objects::nonNull)
                        .map(transformFileHash()).toList();
        return new LogicalFilesSearchResult(names.toArray(new LogicalFileValue[0]));
    }

    @GetMapping(value = "imgash/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] imageById(@RequestParam(name = "id") String id) {
        logger.debug("Retrieving image from id... [id={}]", id);
        Optional<SmallFileData> image = fileIndexService.getSmallFileById(UUID.fromString(id));

        return image.map(smallFileData -> smallFileData.getBlob().array()).orElse(null);
    }

    @PostMapping(value = "imgash/compare", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String compareImageHash(@RequestBody CompareHashPairRequest compareHashPairRequest) {
        logger.debug("Retrieving hashes from index...");

        Iterable<UUID> ids=
        Stream.of(compareHashPairRequest.getIdA(), compareHashPairRequest.getIdB()).map(UUID::fromString).toList();

        int result = fileIndexService.getFileHashComparison(ids);
        return String.valueOf(result);
    }

    private static Function<FileMetaData, LogicalFileValue> transformLogicalFile() {
        return f -> {
            return new LogicalFileValue(
                    f.getId().toString(), // String id,
                    f.getDevicePath(), // String devicePath,
                    f.getUpdated_date(), // Date date,
                    f.getScanTime(), // long scanTime,
                    f.getDeviceId().toString(), // String deviceId,
                    f.getProperties()); // Map<String, String> properties
        };

    }

    private static Function<FileHashData, LogicalFileValue> transformFileHash() {
        return f -> {

            Map<String, String> data = new HashMap<>();
            data.put("checksum", f.getHash());
            data.put("smallfiledataid", f.getSmallFileDataId().toString());

            return new LogicalFileValue(
                    f.getId().toString(), // String id,
                    f.getDevicePath(), // String devicePath,
                    f.getUpdated_date(), // Date date,
                    f.getScanTime(), // long scanTime,
                    f.getDeviceId().toString(), // String deviceId,
                    data); // Map<String, String> properties
        };

    }

}
