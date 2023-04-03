package org.se.mac.blorksandbox.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, ScannerService.class})
class ScannerServiceTest {

    @MockBean
    private LogicalFileIndexService logicalFileIndexService;

    @Autowired
    private ScannerService scannerService;

    @AfterEach
    public void verifyMocksAfter() {
        verifyNoMoreInteractions(logicalFileIndexService);
    }

    @Test
    void scan_whenNullArg_expectException() {
        try {
            scannerService.scan(null, UUID.randomUUID());
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("\"uri\" is null"));
        }
    }

    @Test
    void scan_whenEmptyArg_expectException() {
        try {
            scannerService.scan(new URI(""), UUID.randomUUID());
            fail("Exception expected");
        } catch (URISyntaxException e) {
            fail("Unexpected Exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Missing scheme"));
        }
    }

    @Test
    void scan_whenNonexistingFileArg_expectException() throws URISyntaxException, InterruptedException {
        try {
            scannerService.scan(new URI("file:///apa"), UUID.randomUUID());
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Not a directory"));
        }
    }

    @Test
    void scan_whenTestDirectoryArg_expectOk() throws InterruptedException {
        File f = new File("./test-filestructure");
        boolean result = scannerService.scan(f.getAbsoluteFile().toURI(), UUID.randomUUID());
        assertTrue(result);
        verify(logicalFileIndexService, times(12))
                .add(
                        any(UUID.class),
                        any(Instant.class),
                        anyString(),
                        anyMap(),
                        anyLong()
                );
    }

    @Test
    void scan_whenSingleExifFileArg_expectException() throws InterruptedException {
        try {
            File f = new File("./test-filestructure/exif-org/nikon-e950.jpg");
            scannerService.scan(f.getAbsoluteFile().toURI(), UUID.randomUUID());
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Not a directory"));
        }
    }

    @Test
    void scan_whenExifDriectoryArg_expectOk() throws InterruptedException {
        File f = new File("./test-filestructure/single-exif-org");
        boolean result = scannerService.scan(f.getAbsoluteFile().toURI(), UUID.randomUUID());
        assertTrue(result);

        ArgumentCaptor<Map> argumentsCaptured = ArgumentCaptor.forClass(Map.class);
        //noinspection unchecked
        verify(logicalFileIndexService)
                .add(
                        any(UUID.class),
                        any(Instant.class),
                        ArgumentMatchers.contains("sony-powershota5.jpg"),
                        argumentsCaptured.capture(),
                        anyLong()
                        /*
                        any(UUID.class),
                        any(Instant.class),
                        anyString(),
                        anyMap(),
                        anyLong()
                         */
                );
        var foo = argumentsCaptured.getValue();
        assert argumentsCaptured.getValue().containsKey("File Size");
        assert argumentsCaptured.getValue().containsKey("File Name");

    }
}