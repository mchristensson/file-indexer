package org.se.mac.blorksandbox.jobqueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.jobqueue.job.QueuedJob;
import org.se.mac.blorksandbox.scanner.job.FileScannerJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, QueueService.class})
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @AfterEach
    public void verifyMocksAfter() {

    }

    @Test
    void enqueue() {
        QueuedJob jobMock = mock(FileScannerJob.class);
        queueService.enqueue(jobMock);
        assertTrue(queueService.isRunning());
        assertEquals(1, queueService.getResult().size());
        verify(jobMock).getId();

        verifyNoMoreInteractions(jobMock);
    }

    @Test
    void getResult() {
        Map<Long, Integer> map = queueService.getResult();
        assertNotNull(map);
    }
}