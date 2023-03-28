package org.se.mac.blorksandbox.scanner;

import com.drew.imaging.ImageProcessingException;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.task.DummyAnalyzerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * @param uri URI targeting the device location to be analyzed
     * @return true after successful scan
     * @throws RuntimeException If invalid directory
     */
    public boolean scan(URI uri) {
        UUID deviceId = UUID.randomUUID();
        logger.debug("Inside ScannerService - launching scan with URI {}, deviceId={}", uri, deviceId);
        Path path = Paths.get(uri);
        if (!Files.isDirectory(path)) {
            throw new RuntimeException("Not a directory");
        }
        scanFolder(deviceId, path, true);
        logger.debug("Inside ScannerService, completed.");
        return true;
    }

    /**
     * Traverses through the files inside a directory and performs meta-data extraction
     *
     * @param deviceId  Unique identifier for the scanned device
     * @param path      Path known to be a directory
     * @param recursive Control flag whether to include sub-directories
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
        DummyAnalyzerTask task = new DummyAnalyzerTask();
        try {
            Map<String, String> data = task.apply(path);
            logger.debug("Adding data to index... [properties={}]", data);
            logicalFileIndexService.add(deviceId, path.toString(), data);
        } catch (Exception e) {
            if (!(e instanceof ImageProcessingException)) {
                throw new RuntimeException(e);
            }
        }
    }

}



