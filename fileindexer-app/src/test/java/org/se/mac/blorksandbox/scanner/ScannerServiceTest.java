package org.se.mac.blorksandbox.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

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
                .createFileMetaData(
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
                .createFileMetaData(
                        any(UUID.class),
                        any(Instant.class),
                        ArgumentMatchers.contains("sony-powershota5.jpg"),
                        argumentsCaptured.capture(),
                        anyLong()
                );
        var foo = argumentsCaptured.getValue();
        assert argumentsCaptured.getValue().containsKey("File Size");
        assert argumentsCaptured.getValue().containsKey("File Name");

    }

    @Test
    void generateImageHash_whenNullArgs_expectException() {
        URI uri = null;
        UUID deviceId = null;
        try {
            scannerService.generateImageHash(uri, deviceId);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    void generateImageHash_whenNullUri_expectException() {
        URI uri = null;
        UUID deviceId = UUID.randomUUID();
        try {
            scannerService.generateImageHash(uri, deviceId);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertTrue(e.getMessage().contains("\"uri\" is null"));
        }
    }

    @Test
    void generateImageHash_whenNullDeviceId_expectException() throws IOException {
        File f = File.createTempFile("test", "tmp");
        f.deleteOnExit();
        URI uri = f.toURI();
        UUID deviceId = null;
        try {
            scannerService.generateImageHash(uri, deviceId);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
        }
    }

    @Test
    void generateImageHash_whenImageInvalid_expectException() throws IOException {
        File f = File.createTempFile("test", "tmp");
        f.deleteOnExit();
        URI uri = f.toURI();
        UUID deviceId = UUID.randomUUID();
        try {
            scannerService.generateImageHash(uri, deviceId);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
        }
    }

    @Test
    void generateImageHash_whenArgs_expect() throws IOException {

        /*
        public SmallFileData createSmallFile(     @Validated UUID deviceId,
    Instant timestamp,
    String devicePath,
    ByteBuffer data,
    String contentType )
         */
        SmallFileData smallFileDataMock = mock(SmallFileData.class);
        when(smallFileDataMock.getId()).thenReturn(UUID.randomUUID());
        when(logicalFileIndexService.createSmallFile(
                any(UUID.class),
                any(Instant.class),
                anyString(),
                any(ByteBuffer.class),
                anyString()
        )).thenReturn(smallFileDataMock);

        FileHashData fileHashDataMock = mock(FileHashData.class);
        when(logicalFileIndexService.createFileHash(
                any(UUID.class),
                any(Instant.class),
                anyString(),
                anyString(), anyLong())).thenReturn(fileHashDataMock);

        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        Path targetPath = Path.of(tmpdir.getPath(), "foo.png");
        File targetFile = targetPath.toFile();
        targetFile.deleteOnExit();
        try (InputStream is = getClass().getResourceAsStream("/images/misc/dice-2.png")) {
            System.out.println("Copy file to " + targetPath);
            Files.copy(is, targetPath);
        }
        URI uri = targetFile.toURI();
        UUID deviceId = UUID.randomUUID();

        boolean result = scannerService.generateImageHash(uri, deviceId);

        assertTrue(result);
        verify(logicalFileIndexService).createFileHash(eq(deviceId), any(Instant.class),
                anyString(), anyString(), anyLong());
        verify(logicalFileIndexService).createSmallFile(eq(deviceId), any(Instant.class),
                anyString(), any(ByteBuffer.class), eq("JPG"));
        verify(logicalFileIndexService).updateFileHashData(eq(fileHashDataMock), any(Consumer.class),
                any(UUID.class));
        verify(smallFileDataMock).getId();
        verifyNoMoreInteractions(smallFileDataMock, fileHashDataMock);
    }

}