package org.se.mac.blorksandbox.jobqueue.job;

import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.job.FileHashAnalyzerJob;
import org.se.mac.blorksandbox.scanner.model.UrlType;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class DummyJobTest {

    @Test
    void getCreated() {
        DummyJob job = new DummyJob();
        long created = job.getCreated();
        assertNotNull(created);
    }

    @Test
    void getTask() {
        DummyJob job = new DummyJob();
        Callable<Integer> task = job.getTask();
        assertNotNull(task);
    }

    @Test
    void getId() {
        DummyJob job = new DummyJob();
        long id = job.getId();
        assertNotNull(id);
    }
}