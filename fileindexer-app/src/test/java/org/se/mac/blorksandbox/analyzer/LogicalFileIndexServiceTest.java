package org.se.mac.blorksandbox.analyzer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.se.mac.blorksandbox.analyzer.data.LogicalFileData;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, LogicalFileIndexService.class})
class LogicalFileIndexServiceTest {

    @MockBean
    private LogicalFileRepository repository;

    @Autowired
    private LogicalFileIndexService logicalFileIndexService;

    @AfterEach
    public void verifyMocksAfter() {
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAll_whenNoDataPresent_expectResult() {
        Collection<LogicalFileData> mockResult =  new ArrayList<>();
        when(logicalFileIndexService.getAll()).thenReturn(mockResult);
        Collection<LogicalFileData> searchResult = logicalFileIndexService.getAll();
        assertEquals(0, searchResult.size());
        verify(repository).findAll();
    }

    @Test
    void getAll_whenDataPresent_expectResult() {
        Collection<LogicalFileData> mockResult =  new ArrayList<>();
        LogicalFileData mock1 = mock(LogicalFileData.class);
        LogicalFileData mock2 = mock(LogicalFileData.class);
        mockResult.add(mock1);
        mockResult.add(mock2);
        when(logicalFileIndexService.getAll()).thenReturn(mockResult);
        Collection<LogicalFileData> searchResult = logicalFileIndexService.getAll();
        assertEquals(2, searchResult.size());
        verify(repository).findAll();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void add_whenNullDeviceId_expectAdded() {
        Instant timestamp = Instant.now();
        logicalFileIndexService.add(null, timestamp, "c:/abc/def/ghi.pcx", null, 100L);
        verify(repository).save(any(LogicalFileData.class));
    }

    @Test
    void add_whenNullMap_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Instant timestamp = Instant.now();
        logicalFileIndexService.add(uuid, timestamp, "c:/abc/def/ghi.pcx", null, 100L);
        verify(repository).save(any(LogicalFileData.class));
    }

    @Test
    void add_whenValidValues_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Map<String, String> data = new HashMap<>();
        Instant timestamp = Instant.now();
        logicalFileIndexService.add(uuid,timestamp, "c:/abc/def/ghi.pcx", data, 100L);
        verify(repository).save(any(LogicalFileData.class));
    }
}