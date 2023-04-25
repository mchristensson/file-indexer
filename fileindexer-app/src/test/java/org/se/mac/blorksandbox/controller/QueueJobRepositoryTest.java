package org.se.mac.blorksandbox.controller;

import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.jobqueue.job.DummyJob;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, QueueJobRepository.class, CachingConfig.class})
class QueueJobRepositoryTest {

    @Autowired
    private QueueJobRepository jobComponent;

    @Test
    void lookupByTitle_whenNotPresent_expectOptionalEmpty() {
        assertNotNull(jobComponent);
        Optional<? extends Class<? extends QueuedJob>> result = jobComponent.lookupByTitle("foo");
        assertTrue(result.isEmpty());
    }

    @Test
    void lookupByTitle_whenDevNullJob_expectClassFound() {
        assertNotNull(jobComponent);
        Optional<? extends Class<? extends QueuedJob>> result = jobComponent.lookupByTitle("DevNull job");
        assertFalse(result.isEmpty());
        assertEquals(DummyJob.class, result.get());
    }

    @Test
    void findAll_whenManyPresent_expectManyAndSpecificClassFound() {
        List<String> result = jobComponent.findAllTitles();
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch("DevNull job"::equalsIgnoreCase));
    }

}