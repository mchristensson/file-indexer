package org.se.mac.blorksandbox.scanner.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.se.mac.blorksandbox.analyzer.task.ImageAnalyzerTask;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.model.UrlType;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class FileHashAnalyzerJobTest {

    @Test
    void getTask_whenCostructed_expectCallable() {
        UUID deviceId = UUID.randomUUID();
        String devicePath = "bar";
        UrlType urlType = UrlType.WIN_DRIVE_LETTER;
        ScannerService serviceMock = mock(ScannerService.class);
        FileHashAnalyzerJob job = new FileHashAnalyzerJob(deviceId, devicePath, urlType, serviceMock);

        Callable<Integer> task = job.getTask();
        assertNotNull(task);

        verifyNoInteractions(serviceMock);
    }
}