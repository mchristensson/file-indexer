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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ScannerService {

    private static final Logger logger = LoggerFactory.getLogger(ScannerService.class);

    @Autowired
    private LogicalFileIndexService logicalFileIndexService;

    public boolean scan(URI uri) throws InterruptedException {
        logger.debug("Inside ScannerService - launching scan with URI {}", uri);
        scanFolder(Paths.get(uri), true);
        logger.debug("Inside ScannerService, completed.");
        return true;
    }

    private void scanFolder(final Path path, final boolean recursive) {
        try {
            logger.debug("Scanning directory... [URI: '{}', recursive={}]", path, recursive);
            logger.debug("Scanning directory... [file: '{}']", path.toFile().getAbsoluteFile());
            if (!Files.isDirectory(path)) {
                throw new IOException("Not a directory");
            }
            Files.list(path).forEach(path1 -> {
                if (Files.isDirectory(path1) && recursive) {
                    logger.debug("Processing path (directory): {}", path1);
                    scanFolder(path1, recursive);
                } else {
                    logger.debug("Processing path (file): {}", path1);
                    scanFile(path1);
                }
            });
        } catch (IOException e) {
            logger.error("Unable to process path " + path, e);
            throw new RuntimeException(e);
        }
    }

    private void scanFile(Path path)  {
        logger.debug("Scanning file... [URI: '{}']", path);
        DummyAnalyzerTask task = new DummyAnalyzerTask();
        Map<String, String> data = null;
        try {
            data = task.apply(path);
        } catch (Exception e) {
            if (!(e instanceof ImageProcessingException)) {
                throw new RuntimeException(e);
            }
        }

        //Store the result in Index
        if (Objects.nonNull(data)) {
            logicalFileIndexService.add(path.toString(), data);
        }
    }

}



