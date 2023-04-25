package org.se.mac.blorksandbox.controller;

import org.se.mac.blorksandbox.analyzer.FileTransformationService;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.data.DeviceData;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.jobqueue.QueueService;
import org.se.mac.blorksandbox.jobqueue.rest.QueueJobStatus;
import org.se.mac.blorksandbox.jobqueue.rest.QueuedJobRequestReceipt;
import org.se.mac.blorksandbox.rest.LogicalFilesSearchResult;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.rest.*;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    @Autowired
    private FileTransformationService fileTransformationService;

    @Autowired
    private QueueJobRepository queueJobRepository;

    /**
     * Query and return all enquqable job definitions
     *
     * @return List of all job definition titles
     */
    @GetMapping("queue/jobs")
    public ResponseEntity<List<String>> findAllJobsTitles() {
        logger.debug("Fetching all job definitions...");
        List<String> titles = queueJobRepository.findAllTitles();
        return ResponseEntity.ok(titles);
    }

    /**
     * Return status for all enqueued job tasks
     *
     * @return Status for all enqueued job tasks
     */
    @GetMapping(value = "queue/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public QueueJobStatus queueQueueJobStatus() {
        logger.debug("Retrieving job data from queue...");
        return new QueueJobStatus(queueService.getResult());
    }

    /**
     * Enqueue a new job task
     *
     * @param request Request data
     * @return Acknowledge of enqueued job task
     */
    @PostMapping(value = "queue/enqueue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public QueuedJobRequestReceipt enqueue(@RequestBody ScanEnqueueRequest request) {
        logger.debug("Enqueuing job...");
        Optional<? extends Class<? extends QueuedJob>> def = queueJobRepository.lookupByTitle(request.jobTitle());
        if (def.isEmpty()) {
            return new QueuedJobRequestReceipt(0L, "Job definition " + request.jobTitle() + " could not be found.");
        } else {
            try {
                QueuedJob scanJob = def.get().getDeclaredConstructor().newInstance();
                scanJob.setProperties(null, request.properties());
                queueService.enqueue(scanJob);
                return new QueuedJobRequestReceipt(scanJob.getId(), "Job of type '" + request.jobTitle() + "' was enqueued");

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                logger.error("Unable to enqueue job", e);
                return new QueuedJobRequestReceipt(0L, "Job definition " + request.jobTitle() + " could not be queued." + e.getLocalizedMessage());
            }
        }
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
        Iterable<UUID> ids =
                Stream.of(compareHashPairRequest.idA(), compareHashPairRequest.idB()).map(UUID::fromString).toList();

        int result = fileIndexService.getFileHashComparison(ids);
        return String.valueOf(result);
    }

    @GetMapping(value = "common/device/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LogicalDeviceInfo>> findAllJobsDevices() {
        logger.debug("Retrieved request for a list of all available devices...");
        return ResponseEntity.ok(fileIndexService.getAllDevices().stream()
                .map(deviceData -> transformDeviceData().apply(deviceData))
                .collect(Collectors.toList()));
    }

    /**
     * Create a new device in the corresponding repository
     *
     * @param deviceInfo Data about the device to create
     * @return Id of the newly created Device
     */
    @PostMapping(value = "common/device/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addDevice(@RequestBody LogicalDeviceInfo deviceInfo) {
        logger.debug("Retrieved request for adding a new device... [deviceInfo={}]", deviceInfo);
        return Optional.of(fileIndexService.createDevice(deviceInfo.devicePath(), deviceInfo.title(), deviceInfo.properties()))
                .map(logicalDeviceInfo -> ResponseEntity.ok(logicalDeviceInfo.getId().toString()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Device could not be created"));
    }

    @PostMapping(value = "imgash/transform", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> transformImage(@RequestBody final TransformImageRequest transformImageRequest) {
        logger.debug("Retrieved request for tranformation of image... [request={}]", transformImageRequest);

        //Search for image
        Optional<SmallFileData> image = fileIndexService.getSmallFileById(UUID.fromString(transformImageRequest.imageId()));
        try {
            if (image.isPresent()) {
                UUID uuid = this.fileTransformationService.transformImage(
                        image.get(),
                        transformImageRequest.transformation(),
                        transformImageRequest.imageWidth(),
                        transformImageRequest.imageHeight());
                return ResponseEntity.ok(uuid.toString());
            } else {
                logger.warn("Image could not be found [id={}]", transformImageRequest.imageId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image could not be found");
            }

        } catch (IOException e) {
            logger.error("Unable to transform image");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to transform image");
        }

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

    private Function<DeviceData, LogicalDeviceInfo> transformDeviceData() {
        return f -> new LogicalDeviceInfo(
                f.getId().toString(),
                f.getBasePath(),
                f.getTitle(),
                f.getUpdated_date(),
                f.getProperties());
    }
}
