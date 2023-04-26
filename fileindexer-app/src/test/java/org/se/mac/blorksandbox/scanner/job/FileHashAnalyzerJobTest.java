package org.se.mac.blorksandbox.scanner.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.model.UrlType;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileHashAnalyzerJobTest {

    @Test
    void getTask_whenDeviceIdNotSet_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_PATH, "bar");
        map.put(DirectoryScannerJob.URL_TYPE, UrlType.WIN_DRIVE_LETTER.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            e.printStackTrace();
            assertTrue(e.getMessage().contains("Invalid device ID"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenDeviceIdEmpty_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, "");
        map.put(DirectoryScannerJob.DEVICE_PATH, "bar");
        map.put(DirectoryScannerJob.URL_TYPE, UrlType.WIN_DRIVE_LETTER.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            e.printStackTrace();
            assertTrue(e.getMessage().contains("Invalid device ID"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenDevicePathNotSet_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, UUID.randomUUID().toString());
        map.put(DirectoryScannerJob.URL_TYPE, UrlType.WIN_DRIVE_LETTER.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Invalid device path"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenDevicePathEmpty_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, UUID.randomUUID().toString());
        map.put(DirectoryScannerJob.DEVICE_PATH, "");
        map.put(DirectoryScannerJob.URL_TYPE, UrlType.WIN_DRIVE_LETTER.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            e.printStackTrace();
            assertTrue(e.getMessage().contains("Invalid device path"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenDeviceUrlTypeNotSet_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, UUID.randomUUID().toString());
        map.put(DirectoryScannerJob.DEVICE_PATH, "bar");
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Invalid url type"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenDeviceUrlTypeEmpty_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, UUID.randomUUID().toString());
        map.put(DirectoryScannerJob.DEVICE_PATH, "bar");
        map.put(DirectoryScannerJob.URL_TYPE, "");
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            e.printStackTrace();
            assertTrue(e.getMessage().contains("Invocation requires a valid applicationcontext"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenApplicationContextNotSet_expectException() {
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, UUID.randomUUID().toString());
        map.put(DirectoryScannerJob.DEVICE_PATH, "bar");
        map.put(DirectoryScannerJob.URL_TYPE, UrlType.WIN_DRIVE_LETTER.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        try {
            job.setProperties(map);
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Invocation requires a valid applicationcontext"));
        }
        verifyNoMoreInteractions(ctxMock);
    }

    @Test
    void getTask_whenConstructed_expectCallable() {
        ScannerService serviceMock = mock(ScannerService.class);
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, UUID.randomUUID().toString());
        map.put(DirectoryScannerJob.DEVICE_PATH, "bar");
        map.put(DirectoryScannerJob.URL_TYPE, UrlType.WIN_DRIVE_LETTER.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        when(ctxMock.getBean(ScannerService.class)).thenReturn(serviceMock);
        job.setApplicationContext(ctxMock);
        job.setProperties(map);

        Callable<Integer> task = job.getTask();
        assertNotNull(task);

        verifyNoMoreInteractions(ctxMock);
        verifyNoInteractions(serviceMock);
    }
}