package org.se.mac.blorksandbox.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.image.SaveImageToDiskSupport;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

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
    void scan_whenNonexistingFileArg_expectException() throws URISyntaxException {
        try {
            scannerService.scan(new URI("file:///apa"), UUID.randomUUID());
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Not a directory"));
        }
    }

    @Test
    void scan_whenDeviceNotPresent_expectException() {
        File f = new File("./test-filestructure");
        try {
            scannerService.scan(f.getAbsoluteFile().toURI(), UUID.randomUUID());
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Device does not exist"));
        }
        verify(logicalFileIndexService).isDevicePresent(any(UUID.class));
    }

    @Test
    void scan_whenTestDirectoryArg_expectOk() {
        File f = new File("./test-filestructure");
        UUID deviceId = UUID.randomUUID();
        when(logicalFileIndexService.isDevicePresent(deviceId)).thenReturn(true);
        boolean result = scannerService.scan(f.getAbsoluteFile().toURI(), deviceId);
        assertTrue(result);
        verify(logicalFileIndexService, times(709)).createFileMetaData(any(UUID.class),
                any(Instant.class), anyString(), anyMap(), anyLong());
        verify(logicalFileIndexService).isDevicePresent(deviceId);
    }

    @Test
    void scan_whenSingleExifFileArg_expectException() {
        try {
            File f = new File("./test-filestructure/exif-org/nikon-e950.jpg");
            scannerService.scan(f.getAbsoluteFile().toURI(), UUID.randomUUID());
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Not a directory"));
        }
    }

    @Test
    void scan_whenExifDriectoryArg_expectOk() {
        File f = new File("./test-filestructure/single-exif-org");
        UUID deviceId = UUID.randomUUID();
        when(logicalFileIndexService.isDevicePresent(deviceId)).thenReturn(true);
        boolean result = scannerService.scan(f.getAbsoluteFile().toURI(), deviceId);
        assertTrue(result);

        ArgumentCaptor<Map> argumentsCaptured = ArgumentCaptor.forClass(Map.class);

        //noinspection unchecked
        verify(logicalFileIndexService).createFileMetaData(any(UUID.class), any(Instant.class),
                ArgumentMatchers.contains("sony-powershota5.jpg"), argumentsCaptured.capture(),
                anyLong());
        verify(logicalFileIndexService).isDevicePresent(deviceId);

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
        verify(logicalFileIndexService).isDevicePresent(any(UUID.class));
    }

    @Test
    void generateImageHash_whenArgs_expect() throws IOException {
        SmallFileData smallFileDataMock = mock(SmallFileData.class);
        when(smallFileDataMock.getId()).thenReturn(UUID.randomUUID());
        when(logicalFileIndexService.createSmallFile(any(UUID.class), anyString(),
                any(ByteBuffer.class), anyString(), any(Instant.class))).thenReturn(
                smallFileDataMock);

        FileHashData fileHashDataMock = mock(FileHashData.class);
        when(logicalFileIndexService.createFileHash(any(UUID.class), any(Instant.class),
                anyString(), anyString(), anyLong())).thenReturn(fileHashDataMock);


        Path targetPath = SaveImageToDiskSupport.copyToTmp(
                getClass().getResourceAsStream("/images/misc/dice-2.png"), "foo.png", true);

        UUID deviceId = UUID.randomUUID();
        when(logicalFileIndexService.isDevicePresent(deviceId)).thenReturn(true);
        boolean result = scannerService.generateImageHash(targetPath.toUri(), deviceId);

        assertTrue(result);
        verify(logicalFileIndexService).createFileHash(eq(deviceId), any(Instant.class),
                anyString(), anyString(), anyLong());
        verify(logicalFileIndexService).createSmallFile(eq(deviceId), eq(targetPath), anyString(),
                eq("JPG"));
        verify(logicalFileIndexService).isDevicePresent(deviceId);
        verifyNoMoreInteractions(smallFileDataMock, fileHashDataMock);
    }

}