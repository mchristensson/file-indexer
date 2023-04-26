package org.se.mac.blorksandbox.scanner;

import com.drew.imaging.ImageProcessingException;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.task.FileAnalyzerTask;
import org.se.mac.blorksandbox.analyzer.task.ImageAnalyzerTask;
import org.se.mac.blorksandbox.analyzer.task.ImageHashGeneratorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class ScannerService {

    private static final Logger logger = LoggerFactory.getLogger(ScannerService.class);

    @Autowired
    private LogicalFileIndexService logicalFileIndexService;

    /**
     * Performs meta-data extraction on a directory on a file-device
     *
     * @param uri      URI targeting the device location to be analyzed
     * @param deviceId Unique identifier for the scanned device
     * @return true after successful scan
     * @throws RuntimeException If invalid directory
     */
    public boolean scan(URI uri, UUID deviceId) {
        logger.debug("Inside ScannerService - launching scan with URI '{}', deviceId='{}'", uri, deviceId);
        Path path = Paths.get(uri);
        if (!Files.isDirectory(path)) {
            throw new RuntimeException("Not a directory");
        }
        logger.debug("Validating device...");
        if (deviceId == null || !logicalFileIndexService.isDevicePresent(deviceId)) {
            throw new RuntimeException("Device does not exist");
        }
        scanFolder(deviceId, path, true);
        logger.debug("Inside ScannerService, completed.");
        return true;
    }

    /**
     * Performs hash analysis on a file
     *
     * @param uri      URI targeting the device location to be analyzed
     * @param deviceId Unique identifier for the scanned device
     * @return true after successful scan
     * @throws RuntimeException If invalid directory
     */
    public boolean generateImageHash(URI uri, UUID deviceId) {
        logger.debug("Inside ScannerService - launching generateHash job with URI '{}', deviceId='{}'", uri, deviceId);
        Path path = Paths.get(uri);
        if (Files.isDirectory(path)) {
            throw new RuntimeException("Is a directory");
        }
        logger.debug("Validating device...");
        if (deviceId == null || !logicalFileIndexService.isDevicePresent(deviceId)) {
            throw new RuntimeException("Device does not exist");
        }
        generateHashForFile(deviceId, path);
        logger.debug("Inside ScannerService, completed.");
        return true;
    }

    /**
     * Traverses through the files inside a directory and performs meta-data extraction
     *
     * @param deviceId  Unique identifier for the scanned device
     * @param path      Path known to be a directory
     * @param recursive Control flag whether to include subdirectories
     */
    private void scanFolder(UUID deviceId, final Path path, final boolean recursive) {
        try {
            logger.debug("Scanning directory... [URI: '{}', recursive={}, deviceId='{}']", path, recursive, deviceId);
            Files.list(path).forEach(path1 -> {
                if (Files.isDirectory(path1) && recursive) {
                    scanFolder(deviceId, path1, recursive);
                } else {
                    scanFile(deviceId, path1);
                }
            });
        } catch (IOException e) {
            logger.error("Unable to process path " + path, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts meta-data from a single file
     *
     * @param deviceId Unique identifier for the scanned device
     * @param path     path representing a file (not a directory/folder)
     */
    private void scanFile(UUID deviceId, Path path) {
        logger.debug("Scanning file... [URI: '{}']", path);
        FileAnalyzerTask<Map<String, String>> task = new ImageAnalyzerTask();
        try {
            long t0 = System.currentTimeMillis();
            final Map<String, String> data = task.apply(path);
            long t1 = System.currentTimeMillis();

            logger.debug("Adding data to index... [properties={}]", data);
            logicalFileIndexService.createFileMetaData(deviceId, Instant.ofEpochMilli(t1), path.toString(), data, t1 - t0);
        } catch (Exception e) {
            if (!(e instanceof ImageProcessingException)) {
                throw new RuntimeException(e);
            }
        }
    }

    private UUID smallFileDataId;

    /**
     * Generates hash for image
     *
     * @param path path representing a file (not a directory/folder)
     */
    private void generateHashForFile(UUID deviceId, Path path) {
        logger.debug("Generating has for file... [URI: '{}']", path);
        String procId = UUID.randomUUID().toString();
        String outputFileFormat = "JPG";
        ImageHashGeneratorTask task = new ImageHashGeneratorTask(procId, outputFileFormat, 128, 8);
        try {

            task.setDoAfter((byteBuffer) -> {
                try {
                    smallFileDataId = logicalFileIndexService.createSmallFile(deviceId, path, byteBuffer, outputFileFormat);
                } catch (IOException e) {
                    logger.error("Unable to save file", e);
                }
            });

            long t0 = System.currentTimeMillis();
            final String output = task.apply(path);
            long t1 = System.currentTimeMillis();
            logger.debug("Output hash [output={}, duration={}]", output, t1 - t0);

            FileHashData fileHashData = logicalFileIndexService.createFileHash(deviceId, Instant.ofEpochMilli(t1), path.toString(), output, t1 - t0);

            if (this.smallFileDataId != null) {
                logger.warn("Updating file hash data...");
                logicalFileIndexService.updateFileHashData(fileHashData, fileHashData::setSmallFileDataId, smallFileDataId);
            } else {
                logger.warn("No file hash data to update!");
            }

        } catch (Exception e) {
            if (!(e instanceof ImageProcessingException)) {
                throw new RuntimeException(e);
            }
        }
    }


}


