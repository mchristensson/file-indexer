package org.se.mac.blorksandbox.controller;

import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.analyzer.FileTransformationService;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.jobqueue.QueueService;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class})
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAllJobsTitles() {
        assertNotNull(mockMvc);
    }

    @Test
    void queueQueueJobStatus() {
    }

    @Test
    void enqueue() {
    }

    @Test
    void listLogicalFiles() {
    }

    @Test
    void listImageHash() {
    }

    @Test
    void imageById() {
    }

    @Test
    void compareImageHash() {
    }

    @Test
    void findAllJobsDevices() {
    }

    @Test
    void addDevice() {
    }

    @Test
    void transformImage() {
    }
}