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
    void getTask_whenCostructed_expectCallable() {
        UUID deviceId = UUID.randomUUID();
        String devicePath = "bar";
        UrlType urlType = UrlType.WIN_DRIVE_LETTER;
        ScannerService serviceMock = mock(ScannerService.class);
        ImageFileHashGeneratorJob job = new ImageFileHashGeneratorJob();
        Map<String, String> map = new HashMap<>();
        map.put(DirectoryScannerJob.DEVICE_ID, deviceId.toString());
        map.put(DirectoryScannerJob.DEVICE_PATH, devicePath);
        map.put(DirectoryScannerJob.URL_TYPE, urlType.toString());
        ApplicationContext ctxMock = mock(ApplicationContext.class);
        when(ctxMock.getBean(ScannerService.class)).thenReturn(serviceMock);
        job.setProperties(ctxMock, map);

        Callable<Integer> task = job.getTask();
        assertNotNull(task);

        verifyNoMoreInteractions(ctxMock);
        verifyNoInteractions(serviceMock);
    }
}